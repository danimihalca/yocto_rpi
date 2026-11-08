SUMMARY = "Avahi service advertisements (SSH) for mDNS discovery"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ssh.service"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/avahi/services
    install -m 0644 ${WORKDIR}/ssh.service ${D}${sysconfdir}/avahi/services/ssh.service
}

FILES:${PN} = "${sysconfdir}/avahi/services"
RDEPENDS:${PN} = "avahi-daemon"
