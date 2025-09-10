#!/bin/sh
set -e

# ====== Settings ======
CONNECT_BASE="http://connect:8083"
JSON_PATH="/opt/connect/outbox-connector.json"
CONNECTOR_NAME="mysql-outbox-connector"   # outbox-connector.json의 "name"과 일치해야 함

# ====== Wait for Connect REST ======
echo "[connect-init] waiting for Kafka Connect REST..."
for i in $(seq 1 180); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "$CONNECT_BASE/connector-plugins" || true)
  if [ "$code" = "200" ]; then
    echo "[connect-init] REST is up."
    break
  fi
  sleep 1
  if [ "$i" -eq 180 ]; then
    echo "[connect-init] ERROR: Connect REST not ready in time" >&2
    exit 1
  fi
done

# ====== Prepare JSON (CRLF -> LF, readable) ======
if [ ! -f "$JSON_PATH" ]; then
  echo "[connect-init] ERROR: JSON not found at $JSON_PATH" >&2
  exit 1
fi
sed -i 's/\r$//' "$JSON_PATH" || true
chmod a+r "$JSON_PATH" || true

# ====== Register (POST), handle 409, show response if error ======
echo "[connect-init] registering outbox connector..."
RESP_FILE="/tmp/connect-post.json"
STATUS_CODE=$(curl -s -S -o "$RESP_FILE" -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  --data-binary @"$JSON_PATH" \
  "$CONNECT_BASE/connectors" || true)

if [ "$STATUS_CODE" = "201" ]; then
  echo "[connect-init] created."
elif [ "$STATUS_CODE" = "409" ]; then
  echo "[connect-init] already exists. reconfiguring..."
  curl -s -S -X DELETE "$CONNECT_BASE/connectors/$CONNECTOR_NAME" >/dev/null 2>&1 || true
  sleep 1
  STATUS_CODE=$(curl -s -S -o "$RESP_FILE" -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    --data-binary @"$JSON_PATH" \
    "$CONNECT_BASE/connectors" || true)
  if [ "$STATUS_CODE" = "201" ]; then
    echo "[connect-init] recreated."
  else
    echo "[connect-init] unexpected status after recreate: $STATUS_CODE"
    echo "[connect-init] response body:"
    cat "$RESP_FILE" || true
  fi
elif [ "$STATUS_CODE" = "200" ]; then
  # 일부 배포판은 200을 반환하기도 함
  echo "[connect-init] upserted (200)."
else
  echo "[connect-init] unexpected status: $STATUS_CODE"
  echo "[connect-init] response body:"
  cat "$RESP_FILE" || true
fi

# ====== Print status with retries ======
echo "[connect-init] status (waiting until available)..."
TRIES=60
SLEEP=1
for i in $(seq 1 $TRIES); do
  code=$(curl -s -o /tmp/status.json -w "%{http_code}" "$CONNECT_BASE/connectors/$CONNECTOR_NAME/status" || true)
  if [ "$code" = "200" ]; then
    cat /tmp/status.json
    echo
    break
  fi
  if [ "$i" -eq "$TRIES" ]; then
    echo "[connect-init] WARN: status still not available (last HTTP $code). Listing connectors for debugging:" >&2
    curl -s "$CONNECT_BASE/connectors" || true
    echo
  fi
  sleep $SLEEP
done