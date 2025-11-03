#!/usr/bin/env sh
set -eu

HOST="${1:-db}"
PORT="${2:-5432}"
TIMEOUT="${TIMEOUT:-60}"
SLEEP="${SLEEP:-2}"

end_time=$(( $(date +%s) + TIMEOUT ))

log() {
  printf '[wait-for-db] %s\n' "$1"
}

check_pg_isready() {
  if command -v pg_isready >/dev/null 2>&1; then
    pg_isready -h "$HOST" -p "$PORT" >/dev/null 2>&1
  else
    return 1
  fi
}

check_nc() {
  if command -v nc >/dev/null 2>&1; then
    nc -z "$HOST" "$PORT" >/dev/null 2>&1
  else
    return 1
  fi
}

check_python() {
  if command -v python3 >/dev/null 2>&1; then
    python3 - "$HOST" "$PORT" <<'PY' >/dev/null 2>&1
import socket
import sys
host, port = sys.argv[1], int(sys.argv[2])
with socket.create_connection((host, port), timeout=2):
    pass
PY
  else
    return 1
  fi
}

log "waiting for ${HOST}:${PORT} (timeout ${TIMEOUT}s)"
while [ $(date +%s) -lt "$end_time" ]; do
  if check_pg_isready || check_nc || check_python; then
    log "database is reachable"
    exit 0
  fi
  sleep "$SLEEP"
done

log "timed out waiting for ${HOST}:${PORT}"
exit 1
