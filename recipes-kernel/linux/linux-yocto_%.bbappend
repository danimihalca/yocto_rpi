# Kernel options the lt7680-drm module needs. Only applies to linux-yocto based kernels;
# for a vendor kernel (linux-imx, linux-raspberrypi, ...) add the same fragment to its bbappend.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://lt7680-drm.cfg"
