#!/usr/bin/env bash
# Publish a RAUC bundle to the update server's document root.
# usage: publish.sh <bundle.raucb> <version> [docroot]
set -euo pipefail

bundle=${1:?usage: publish.sh <bundle.raucb> <version> [docroot]}
version=${2:?usage: publish.sh <bundle.raucb> <version> [docroot]}
root=${3:-$(dirname "$0")/ota-root}
channel=rpi4-rauc   # must match OTA_CHANNEL on the device

dir="$root/$channel"
mkdir -p "$dir"

name=$(basename "$bundle")
cp -f "$bundle" "$dir/$name"
sha=$(sha256sum "$dir/$name" | cut -d' ' -f1)

# Write the manifest last and atomically, so devices never see a half-published release
tmp=$(mktemp "$dir/.manifest.XXXXXX")
printf '{\n  "version": "%s",\n  "bundle": "%s",\n  "sha256": "%s"\n}\n' "$version" "$name" "$sha" > "$tmp"
chmod 644 "$tmp"
mv -f "$tmp" "$dir/manifest.json"

echo "Published $name as version $version in $dir"
