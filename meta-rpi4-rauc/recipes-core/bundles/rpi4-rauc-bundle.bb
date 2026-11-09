SUMMARY = "RAUC update bundle for rpi4-rauc-image"
LICENSE = "MIT"

inherit bundle

# "verity" bundles are recommended (and needed for streaming); needs dm-verity in the kernel
RAUC_BUNDLE_FORMAT = "verity"

# Must match `compatible` in system.conf
RAUC_BUNDLE_COMPATIBLE = "rpi4-rauc"
RAUC_BUNDLE_VERSION ?= "0.0.1"
RAUC_BUNDLE_DESCRIPTION = "rpi4-rauc rootfs bundle"

RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_SLOT_rootfs = "rpi4-rauc-image"
RAUC_SLOT_rootfs[fstype] = "ext4"

# Dev keys created by scripts/gen-dev-keys.sh (replace with a real PKI for production)
RAUC_KEYS_DIR ?= "${TOPDIR}/../keys"
RAUC_KEY_FILE = "${RAUC_KEYS_DIR}/dev.key.pem"
RAUC_CERT_FILE = "${RAUC_KEYS_DIR}/dev.cert.pem"
