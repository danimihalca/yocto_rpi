#!/usr/bin/env bash
# Generate a DEV-ONLY CA + signing key pair for RAUC and install the CA into the layer.
# For production use a proper PKI and keep the signing key off the build host.
set -euo pipefail

root=$(cd "$(dirname "$0")/.." && pwd)
keys="$root/keys"
mkdir -p "$keys"
cd "$keys"

if [ -e ca.key.pem ]; then
    echo "keys/ already exists, not overwriting" >&2
    exit 1
fi

openssl req -x509 -newkey rsa:4096 -nodes -days 3650 \
    -keyout ca.key.pem -out ca.cert.pem \
    -subj "/O=rpi4-rauc/CN=rpi4-rauc dev CA" \
    -addext "basicConstraints=critical,CA:TRUE" \
    -addext "keyUsage=critical,keyCertSign,cRLSign"

openssl req -newkey rsa:4096 -nodes \
    -keyout dev.key.pem -out dev.csr.pem \
    -subj "/O=rpi4-rauc/CN=rpi4-rauc dev signer"

ext=$(mktemp)
printf 'keyUsage=critical,digitalSignature\nextendedKeyUsage=codeSigning\n' > "$ext"
openssl x509 -req -in dev.csr.pem -CA ca.cert.pem -CAkey ca.key.pem -CAcreateserial \
    -days 825 -extfile "$ext" -out dev.cert.pem
rm -f "$ext" dev.csr.pem

dest="$root/meta-rpi4-rauc/recipes-core/rauc/files/ca.cert.pem"
cp ca.cert.pem "$dest"
echo "CA certificate installed to $dest"
