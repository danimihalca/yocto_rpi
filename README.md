# Yocto for Raspberry Pi Zero 2W build via KAS

## Instructions

Setup environment:
```
nix-shell
```

Checkout:
```
kas-container checkout yocto_5.0_zero2W.yml
```

Build:
```
kas-container build yocto_5.0_zero2W.yml
```

Flash (outside nix shell):
```
sudo dd if=build/tmp/deploy/images/raspberrypi0-2w-64/core-image-minimal-raspberrypi0-2w-64.rootfs-<LATEST>.wic of=/dev/<TARGET> bs=4M status=progress
```

