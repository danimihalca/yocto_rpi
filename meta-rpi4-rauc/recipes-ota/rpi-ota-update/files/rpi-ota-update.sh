#!/bin/sh
# rpi-ota-update: fetch manifest.json from the LAN server, download the signed
# RAUC bundle if the version differs, verify, install into the inactive slot, reboot.
# RAUC itself verifies the bundle signature against /etc/rauc/ca.cert.pem.
set -eu

CONF=${RPI_OTA_CONF:-/etc/rpi-ota.conf}
[ -r "$CONF" ] && . "$CONF"
: "${OTA_SERVER_URL:?OTA_SERVER_URL not set in $CONF}"
OTA_CHANNEL=${OTA_CHANNEL:-rpi4-rauc}
OTA_AUTO_REBOOT=${OTA_AUTO_REBOOT:-1}
WORKDIR=${OTA_WORKDIR:-/data/ota}
BASE="${OTA_SERVER_URL%/}/${OTA_CHANNEL}"

log() { echo "rpi-ota: $*"; }

# Installed but not yet rebooted: nothing to do until the next boot
[ -e /run/rpi-ota-pending ] && { log "update installed, waiting for reboot"; exit 0; }

mkdir -p "$WORKDIR"
exec 9>"$WORKDIR/lock"
flock -n 9 || { log "another run is in progress"; exit 0; }

installed_version() {
    st=$(rauc status --output-format=json) || return 0
    booted=$(printf '%s' "$st" | jq -r '.booted // empty')
    [ -n "$booted" ] || return 0
    printf '%s' "$st" | jq -r --arg b "$booted" \
        '[.slots[] | to_entries[] | select(.value.bootname == $b) | .value.slot_status.bundle.version // empty] | first // empty'
}

current=$(installed_version || true)
current=${current:-none}

# If the last attempt did not stick (rolled back), remember it and don't retry it
if [ -f "$WORKDIR/attempt" ]; then
    attempt=$(cat "$WORKDIR/attempt")
    if [ "$attempt" != "$current" ]; then
        log "update to $attempt did not take effect (running $current); marking as failed"
        echo "$attempt" >> "$WORKDIR/failed"
    fi
    rm -f "$WORKDIR/attempt"
fi

manifest=$(curl -fsS --connect-timeout 5 --max-time 20 "$BASE/manifest.json") \
    || { log "server unreachable, will retry later"; exit 0; }

version=$(printf '%s' "$manifest" | jq -r '.version // empty')
bundle=$(printf '%s' "$manifest" | jq -r '.bundle // empty')
sha256=$(printf '%s' "$manifest" | jq -r '.sha256 // empty')

if [ -z "$version" ] || [ -z "$bundle" ] || [ -z "$sha256" ]; then
    log "malformed manifest"; exit 1
fi
case "$bundle" in
    */*|.*) log "refusing suspicious bundle name: $bundle"; exit 1 ;;
esac

if [ "$version" = "$current" ]; then
    log "already running $current"; exit 0
fi
if [ -f "$WORKDIR/failed" ] && grep -qxF "$version" "$WORKDIR/failed"; then
    log "version $version previously failed, skipping"; exit 0
fi

log "update available: $current -> $version"
dest="$WORKDIR/$bundle"
rm -f "$dest" "$dest.part"
curl -fS --connect-timeout 5 -o "$dest.part" "$BASE/$bundle"
if ! echo "$sha256  $dest.part" | sha256sum -c - >/dev/null 2>&1; then
    log "checksum mismatch"; rm -f "$dest.part"; exit 1
fi
mv "$dest.part" "$dest"

echo "$version" > "$WORKDIR/attempt"
if rauc install "$dest"; then
    rm -f "$dest"
    touch /run/rpi-ota-pending
    log "installed $version into the inactive slot"
    [ "$OTA_AUTO_REBOOT" = "1" ] && systemctl reboot
else
    rm -f "$dest" "$WORKDIR/attempt"
    log "rauc install failed"
    exit 1
fi
