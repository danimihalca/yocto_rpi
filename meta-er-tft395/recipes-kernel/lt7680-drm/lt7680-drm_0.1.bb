SUMMARY = "DRM/KMS driver for the Levetop LT7680 SPI display controller (EastRising ER-TFT3.95-1)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit module

SRC_URI = " \
    file://Makefile \
    file://lt7680-drm.c \
    file://lt7680-nv3052-boe395.h \
"

# scarthgap and older unpack local files into WORKDIR; styhead and newer unpack them into
# UNPACKDIR, which bitbake.conf defines. On scarthgap UNPACKDIR does not exist, so give it a
# weak default of WORKDIR: it only applies when nothing else defined it, and S then works on
# both. (The previous inline-python version, ${@'${WORKDIR}' if d.getVar('UNPACKDIR') ...},
# leaves a literal "${@...}" in S when UNPACKDIR is unset, which is what broke do_unpack.)
UNPACKDIR ??= "${WORKDIR}"
S = "${UNPACKDIR}"

RPROVIDES:${PN} += "kernel-module-lt7680-drm"

KERNEL_MODULE_AUTOLOAD += "lt7680-drm"
