SUMMARY = "Polls a LAN server for signed RAUC bundles and installs them"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://rpi-ota-update.sh \
    file://rpi-ota.conf \
    file://rpi-ota-update.service \
    file://rpi-ota-update.timer \
"

S = "${WORKDIR}"

inherit systemd allarch

SYSTEMD_SERVICE:${PN} = "rpi-ota-update.timer"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} = "rauc curl jq util-linux-flock"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/rpi-ota-update.sh ${D}${sbindir}/rpi-ota-update

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/rpi-ota.conf ${D}${sysconfdir}/rpi-ota.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/rpi-ota-update.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/rpi-ota-update.timer ${D}${systemd_system_unitdir}/
}

CONFFILES:${PN} = "${sysconfdir}/rpi-ota.conf"
FILES:${PN} += " \
    ${systemd_system_unitdir}/rpi-ota-update.service \
    ${systemd_system_unitdir}/rpi-ota-update.timer \
    ${sbindir}/rpi-ota-update.sh \
"
