# meta-bbq20kbd

Yocto layer for the BBQ10/BBQ20 keyboard (i2c_puppet) on Raspberry Pi, branch `scarthgap`.
Recipes and configuration only. The driver is wallComputer's upstream `bbqX0kbd_driver`,
used as it is; the layer only adds a patch on top.

- `recipes-kernel/bbqx0kbd/bbqx0kbd_git.bb`: fetches upstream at a pinned `SRCREV`, applies
  `files/0001-i2c-probe-signature-for-kernel-6.6.patch`, builds the kernel module and the i2c
  overlay (`i2c-bbqX0kbd.dtbo`, deployed to the boot files), and packages the console keymap
  as `bbqx0kbd-keymap`.
- `kas/bbq20kbd.yml`: kas fragment (I2C, overlay in config.txt, boot files, packages, vconsole).
- `scripts/apply-driver-patches.sh`: applies the layer's patches to the `driver/` submodule.

## The patch

Two small hunks in `bbqX0kbd_main.c` / `bbqX0kbd_main.h`: on kernels >= 6.6 an I2C driver's
`probe()` no longer takes the `i2c_device_id` argument, and `linux/version.h` is included for
the `LINUX_VERSION_CODE` check. Checked against upstream `3cd0f97`. When you bump `SRCREV`,
run `scripts/apply-driver-patches.sh` on the submodule at the new commit to see if it still applies.

## Keymap (alt / altgr symbols)

The driver reports the physical key with LEFTALT or RIGHTALT held. The symbols on the keyboard
(`# ( ) _ { } \ [ ] < > &` ...) come from `bbqX0kbd.map`, which on Raspberry Pi OS is installed by
upstream's `installer.sh` (`KMAP=` in `/etc/default/keyboard`). Here the recipe installs the map
under `/usr/share/keymaps/i386/qwerty/` and writes `/etc/vconsole.conf` (`KEYMAP=bbqX0kbd`);
`systemd-vconsole-setup` loads it at boot. It needs `kbd` (pulled in by `bbqx0kbd-keymap`).
The map affects the text console only, not a desktop.

## `driver/` submodule

`driver/` is a submodule of upstream `wallComputer/bbqX0kbd_driver` (see `.gitmodules`), for
reading the code and for non-Yocto builds. Check out the pinned commit and patch it:

```sh
git submodule add https://github.com/wallComputer/bbqX0kbd_driver.git driver
git -C driver checkout 3cd0f977e4f266a7eb82bbea8fd7d5aabbdb04f2
scripts/apply-driver-patches.sh     # then: cd driver && ./installer.sh   (Raspberry Pi OS)
```

The recipe does not use the submodule. `do_configure` edits `source/mod_src/config.h` with
`sed`; in the recipe that happens in the work directory, so the submodule stays clean.
Settings: `BBQX0KBD_TYPE`, `BBQ20KBD_TRACKPAD_USE`, `BBQX0KBD_INT`, `BBQX0KBD_INT_PIN`,
`BBQX0KBD_POLL_PERIOD` (same names as upstream's installer options).
