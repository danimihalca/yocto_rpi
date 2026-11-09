SUMMARY = "Raspberry Pi 4 image with RAUC A/B updates and a LAN OTA client"
LICENSE = "MIT"

require recipes-core/images/rpi-image.bb

IMAGE_INSTALL:append = " \
    rauc \
    libubootenv-bin \
    kernel-image \
    kernel-modules \
    rpi-ota-update \
    jq \
    curl \
    e2fsprogs-resize2fs \
"
