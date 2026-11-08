# ER-TFT3.95-1 on Raspberry Pi boards (Zero 2 W, 4 B; 32- or 64-bit machines).
# In local.conf:   require conf/er-tft395.inc      (or include it, to ignore it when missing)
ENABLE_SPI_BUS = "1"
RPI_KERNEL_DEVICETREE_OVERLAYS:append = " overlays/er-tft395.dtbo"
IMAGE_INSTALL:append = " lt7680-drm"
KERNEL_MODULE_AUTOLOAD:append = " lt7680-drm"

# SPI clock: 31.25 MHz worked on a Pi 4 B (jumper wires). Zero 2 W is untested, lower it if
# the picture breaks, e.g.  RPI_EXTRA_CONFIG:append:raspberrypi0-2w-64 = "\ndtoverlay=er-tft395,speed=20000000\n"
