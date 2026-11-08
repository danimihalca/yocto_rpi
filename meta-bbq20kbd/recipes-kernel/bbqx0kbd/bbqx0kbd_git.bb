SUMMARY = "BBQ10/BBQ20 keyboard I2C input driver (i2c_puppet) plus Raspberry Pi overlay"
DESCRIPTION = "wallComputer/bbqX0kbd_driver kernel module (one 4-line i2c probe() compatibility patch for kernels >= 6.6), built out of tree, \
plus its i2c1 device tree overlay compiled to a .dtbo and deployed to the boot partition."
HOMEPAGE = "https://github.com/wallComputer/bbqX0kbd_driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9450224a11928f85794c948d3539a882"

SRC_URI = "git://github.com/wallComputer/bbqX0kbd_driver.git;protocol=https;branch=main \
           file://0001-i2c-probe-signature-for-kernel-6.6.patch \
"
SRCREV = "3cd0f977e4f266a7eb82bbea8fd7d5aabbdb04f2"
PV = "0.1+git"

S = "${WORKDIR}/git"

inherit module deploy

DEPENDS += "dtc-native"

# Driver options live in source/mod_src/config.h. The defaults below are the
# upstream defaults, so the only source change is the probe() patch above.
BBQX0KBD_TYPE ?= "BBQ20KBD_PMOD"
BBQ20KBD_TRACKPAD_USE ?= "BBQ20KBD_TRACKPAD_AS_MOUSE"
BBQX0KBD_INT ?= "BBQX0KBD_NO_INT"
BBQX0KBD_POLL_PERIOD ?= "40"
BBQX0KBD_INT_PIN ?= "4"

# The Makefile finds the kernel through KDIR (defaults to the build host's uname -r)
EXTRA_OEMAKE += "KDIR=${STAGING_KERNEL_DIR}"

do_configure() {
    CFG=${S}/source/mod_src/config.h
    sed -i "s/BBQX0KBD_TYPE BBQ.*/BBQX0KBD_TYPE ${BBQX0KBD_TYPE}/" ${CFG}
    sed -i "s/BBQ20KBD_TRACKPAD_USE BBQ20KBD_TRACKPAD_AS.*/BBQ20KBD_TRACKPAD_USE ${BBQ20KBD_TRACKPAD_USE}/" ${CFG}
    sed -i "s/BBQX0KBD_INT BBQ.*/BBQX0KBD_INT ${BBQX0KBD_INT}/" ${CFG}
    sed -i "s/BBQX0KBD_INT_PIN .*/BBQX0KBD_INT_PIN ${BBQX0KBD_INT_PIN}/" ${CFG}
    sed -i "s/BBQX0KBD_POLL_PERIOD .*/BBQX0KBD_POLL_PERIOD ${BBQX0KBD_POLL_PERIOD}/" ${CFG}
}

def bbq_dts(d):
    return "i2c-bbqX0kbd-int.dts" if d.getVar("BBQX0KBD_INT") == "BBQX0KBD_USE_INT" else "i2c-bbqX0kbd-polling.dts"
BBQX0KBD_DTS = "${@bbq_dts(d)}"

do_compile:append() {
    dtc -@ -I dts -O dtb -o ${B}/i2c-bbqX0kbd.dtbo ${S}/source/dts_src/${BBQX0KBD_DTS}
}

# The upstream Makefile has no modules_install target, so install by hand.
do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${B}/bbqX0kbd.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
}

do_deploy() {
    install -d ${DEPLOYDIR}/overlays
    install -m 0644 ${B}/i2c-bbqX0kbd.dtbo ${DEPLOYDIR}/overlays/i2c-bbqX0kbd.dtbo
}
addtask deploy after do_compile before do_build

KERNEL_MODULE_AUTOLOAD += "bbqX0kbd"

COMPATIBLE_MACHINE = "^rpi$"
