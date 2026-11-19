#!/usr/bin/env bash
# Apply this layer's patches to the driver/ submodule (original upstream code), for builds
# that do not go through the Yocto recipe: a plain `make` on Raspberry Pi OS, or externalsrc.
# Safe to run repeatedly: patches that are already applied are skipped.
set -euo pipefail

layer=$(cd "$(dirname "$0")/.." && pwd)
drv="$layer/driver"
[ -e "$drv/.git" ] || { echo "driver/ submodule not checked out: git submodule update --init" >&2; exit 1; }

for p in "$layer"/recipes-kernel/bbqx0kbd/files/*.patch; do
    if git -C "$drv" apply --check "$p" 2>/dev/null; then
        git -C "$drv" apply "$p"
        echo "applied  $(basename "$p")"
    elif git -C "$drv" apply --check -R "$p" 2>/dev/null; then
        echo "already  $(basename "$p")"
    else
        echo "DOES NOT APPLY: $(basename "$p")" >&2
        exit 1
    fi
done
