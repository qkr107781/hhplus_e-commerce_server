#!/usr/bin/env bash
set -euo pipefail

INFLUX_URL="${INFLUX_URL:-http://influxdb:8086}"
INFLUX_DB="${INFLUX_DB:-k6}"
INFLUX_DB_METRICS="${INFLUX_DB_METRICS:-metrics}"

# v1 인증 쓰는 경우만 지정 (미사용이면 비워두기)
INFLUX_USER="${INFLUX_USER:-}"
INFLUX_PASS="${INFLUX_PASS:-}"

echo "🔎 Waiting for InfluxDB(v1) at ${INFLUX_URL} ..."
for i in $(seq 1 60); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "${INFLUX_URL}/ping" || true)
  if echo "$code" | grep -qE "204|200"; then
    echo "✅ InfluxDB is up."
    break
  fi
  sleep 1
done

AUTH_QS=""
if [ -n "$INFLUX_USER" ] || [ -n "$INFLUX_PASS" ]; then
  AUTH_QS="&u=${INFLUX_USER}&p=${INFLUX_PASS}"
fi

echo "🗑  DROP DATABASE \"${INFLUX_DB}\" (if exists)"
curl -sS -G "${INFLUX_URL}/query" \
  --data-urlencode "q=DROP DATABASE \"${INFLUX_DB}\"" >/dev/null || true

echo "📦 CREATE DATABASE \"${INFLUX_DB}\""
curl -sS -G "${INFLUX_URL}/query" \
  --data-urlencode "q=CREATE DATABASE \"${INFLUX_DB}\"" >/dev/null

echo "🗑  DROP DATABASE \"${INFLUX_DB_METRICS}\" (if exists)"
curl -sS -G "${INFLUX_URL}/query" \
  --data-urlencode "q=DROP DATABASE \"${INFLUX_DB_METRICS}\"" >/dev/null || true

echo "📦 CREATE DATABASE \"${INFLUX_DB_METRICS}\""
curl -sS -G "${INFLUX_URL}/query" \
  --data-urlencode "q=CREATE DATABASE \"${INFLUX_DB_METRICS}\"" >/dev/null

# (원하면 리텐션 정책 설정)
# curl -sS -G "${INFLUX_URL}/query" \
#   --data-urlencode "q=CREATE RETENTION POLICY \"thirty_days\" ON \"${INFLUX_DB}\" DURATION 30d REPLICATION 1 DEFAULT" >/dev/null

echo "✅ Done. Database \"${INFLUX_DB}\" is clean."