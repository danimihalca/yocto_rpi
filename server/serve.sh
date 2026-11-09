#!/usr/bin/env bash
# Minimal update server for the LAN (plain HTTP; fine for testing).
# For anything permanent, serve the same directory with nginx/caddy instead.
# usage: serve.sh [docroot]   (PORT=8080 by default)
set -euo pipefail

root=${1:-$(dirname "$0")/ota-root}
mkdir -p "$root"
exec python3 -m http.server "${PORT:-8080}" --bind 0.0.0.0 --directory "$root"
