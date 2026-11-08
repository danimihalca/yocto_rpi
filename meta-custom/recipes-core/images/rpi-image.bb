SUMMARY = "Raspberry Pi 4 image with automatic Wi-Fi and mDNS (Avahi)"
LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "ssh-server-openssh debug-tweaks"

# Make sure systemd-networkd (DHCP on wlan0/eth0) starts at boot
ROOTFS_POSTPROCESS_COMMAND += "enable_networkd;"
enable_networkd() {
    install -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants
    ln -sf ${nonarch_libdir}/systemd/system/systemd-networkd.service \
        ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/multi-user.target.wants/systemd-networkd.service
}

IMAGE_INSTALL:append = " \
    wifi-autoconnect \
    avahi-services \
    avahi-daemon \
    avahi-utils \
    libnss-mdns \
    wpa-supplicant \
    iw \
    wireless-regdb-static \
    linux-firmware-rpidistro-bcm43455 \
    kernel-modules \
"
