# ER-TFT3.95-1 on Raspberry Pi boards (Zero 2 W, 4 B; 32- or 64-bit machines).
# In local.conf:   require conf/er-tft395.inc      (or include it, to ignore it when missing)
ENABLE_SPI_BUS = "1"
RPI_KERNEL_DEVICETREE_OVERLAYS:append = " overlays/er-tft395.dtbo"
IMAGE_INSTALL:append = " lt7680-drm"
KERNEL_MODULE_AUTOLOAD:append = " lt7680-drm"

# RPI_KERNEL_DEVICETREE_OVERLAYS only builds and ships the .dtbo; the dtoverlay= line in
# config.txt has to be added separately or the overlay is never loaded.
ER_TFT395_OVERLAY ?= "er-tft395"
RPI_EXTRA_CONFIG:append = "\ndtoverlay=${ER_TFT395_OVERLAY}\n"

# Text console on the panel (see er-tft395.yml for the details).
ER_TFT395_FBCON_ARGS ?= "console=tty1 fbcon=font:TER16x32"
CMDLINE:append = " ${ER_TFT395_FBCON_ARGS}"

# SPI clock: 31.25 MHz worked on a Pi 4 B (jumper wires). Zero 2 W is untested, lower it if
# the picture breaks, e.g.  ER_TFT395_OVERLAY:raspberrypi0-2w-64 = "er-tft395,speed=20000000"
