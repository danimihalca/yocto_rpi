SUMMARY = "Auto-connect wlan0 via wpa_supplicant + systemd-networkd (DHCP)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://20-wlan0.network \
    file://10-eth0.network \
"

S = "${WORKDIR}"

# Values come from the environment (kas passes WIFI_SSID / WIFI_PSK / WIFI_COUNTRY)
WIFI_SSID ??= ""
WIFI_PSK ??= ""
WIFI_COUNTRY ??= "RO"

# Rebuild when credentials change
do_generate_wpa_conf[vardeps] += "WIFI_SSID WIFI_PSK WIFI_COUNTRY"
do_install[vardeps] += "WIFI_SSID WIFI_PSK WIFI_COUNTRY"

python do_generate_wpa_conf() {
    import hashlib, os

    ssid = (d.getVar('WIFI_SSID') or '').strip()
    psk = d.getVar('WIFI_PSK') or ''
    country = (d.getVar('WIFI_COUNTRY') or 'RO').strip()

    lines = [
        'ctrl_interface=/run/wpa_supplicant',
        'ctrl_interface_group=0',
        'update_config=1',
        'country=%s' % country,
        '',
    ]

    if not ssid:
        bb.fatal('WIFI_SSID is empty, so no Wi-Fi network would be configured. '
                 'Build with: WIFI_SSID=... WIFI_PSK=... kas build kas-rpi4.yml '
                 '(with sudo use -E; check with: bitbake -e wifi-autoconnect | grep ^WIFI_)')
    else:
        ssid_hex = ssid.encode('utf-8').hex()
        lines.append('network={')
        lines.append('    ssid=%s' % ssid_hex)   # unquoted = hex-encoded
        if not psk:
            lines.append('    key_mgmt=NONE')
        else:
            if len(psk) == 64:
                psk_hex = psk.lower()
            elif 8 <= len(psk) <= 63:
                psk_hex = hashlib.pbkdf2_hmac('sha1', psk.encode('utf-8'),
                                              ssid.encode('utf-8'), 4096, 32).hex()
            else:
                bb.fatal('WIFI_PSK must be 8-63 characters (or 64 hex digits)')
            lines.append('    psk=%s' % psk_hex)
            lines.append('    key_mgmt=WPA-PSK')
        lines.append('    scan_ssid=1')
        lines.append('}')

    out = os.path.join(d.getVar('WORKDIR'), 'wpa_supplicant-wlan0.conf')
    with open(out, 'w') as f:
        f.write('\n'.join(lines) + '\n')
    os.chmod(out, 0o600)
}
addtask do_generate_wpa_conf after do_unpack before do_install

do_install() {
    install -d ${D}${sysconfdir}/wpa_supplicant
    install -m 0600 ${WORKDIR}/wpa_supplicant-wlan0.conf ${D}${sysconfdir}/wpa_supplicant/

    install -d ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/20-wlan0.network ${D}${sysconfdir}/systemd/network/
    install -m 0644 ${WORKDIR}/10-eth0.network ${D}${sysconfdir}/systemd/network/

    # Enable wpa_supplicant@wlan0.service
    install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
    ln -sf ${nonarch_libdir}/systemd/system/wpa_supplicant@.service \
        ${D}${sysconfdir}/systemd/system/multi-user.target.wants/wpa_supplicant@wlan0.service
}

FILES:${PN} = "${sysconfdir}"
CONFFILES:${PN} = "${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf"

RDEPENDS:${PN} = "wpa-supplicant systemd linux-firmware-rpidistro-bcm43455"
