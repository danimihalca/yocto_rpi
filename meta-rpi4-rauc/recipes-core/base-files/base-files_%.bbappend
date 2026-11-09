# The A/B slots are written from a plain ext4 image (not by wic), so wic cannot
# edit fstab for us. Mount the shared partitions from the rootfs itself.
do_install:append() {
    printf '/dev/mmcblk0p1\t/uboot\tvfat\tdefaults\t0 0\n' >> ${D}${sysconfdir}/fstab
    printf '/dev/mmcblk0p4\t/data\text4\tdefaults\t0 0\n' >> ${D}${sysconfdir}/fstab
    install -d ${D}/uboot ${D}/data

    # U-Boot environment lives as a file on the FAT partition; libubootenv can use a file
    printf '/uboot/uboot.env\t0x0000\t0x4000\n' > ${D}${sysconfdir}/fw_env.config
}

FILES:${PN} += "/uboot /data ${sysconfdir}/fw_env.config"
