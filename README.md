# Yocto for Raspberry Pi 4B build via KAS with ER-TFT-395 via SPI and BBQ20KBD via I2C

## Instructions

Setup environment:
```
nix-shell
```

Checkout:
```
kas-container checkout rpi4_tft_kbd_rauc.yml
```

Set auto WLAN connection params:
```
export WIFI_SSID='<VALUE>' WIFI_PSK='<VALUE>' WIFI_COUNTRY=RO
```

Generate certificate for RAUC:
```
./scripts/gen-dev-keys.sh
```

Build:
```
export RAUC_BUNDLE_VERSION=1.0.0
kas-container --runtime-args "-e RAUC_BUNDLE_VERSION -e WIFI_SSID -e WIFI_PSK -e WIFI_COUNTRY" build rpi4_tft_kbd_rauc.yml
```

Manual flash (outside nix shell):
```
sudo bmaptool copy build/tmp/deploy/images/raspberrypi4-64/rpi4-rauc-image-raspberrypi4-64.rootfs.wic.bz2 /dev/sda
```

OTA update:
```
# Server
./server/publish.sh build/tmp/deploy/images/raspberrypi4-64/rpi4-rauc-bundle-raspberrypi4-64.raucb 1.0.1
./server/serve.sh

# Device
ssh root@rpi4.local
systemctl start rpi-ota-update # or wait for the automatic update timer
```

