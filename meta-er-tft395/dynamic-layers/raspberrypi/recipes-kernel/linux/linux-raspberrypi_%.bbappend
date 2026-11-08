# Raspberry Pi kernel (meta-raspberrypi): kernel options + ER-TFT3.95-1 overlay.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://lt7680-drm.cfg \
    file://er-tft395-overlay.dts \
"

# Build the overlay with the kernel's overlays (er-tft395-overlay.dts -> er-tft395.dtbo)
do_configure:append() {
    cp ${WORKDIR}/er-tft395-overlay.dts ${S}/arch/arm/boot/dts/overlays/er-tft395-overlay.dts
    if ! grep -q "er-tft395.dtbo" ${S}/arch/arm/boot/dts/overlays/Makefile; then
        echo 'dtbo-$(CONFIG_ARCH_BCM2835) += er-tft395.dtbo' >> ${S}/arch/arm/boot/dts/overlays/Makefile
    fi
}
