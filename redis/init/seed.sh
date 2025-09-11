#!/bin/sh
set -eu

REDIS_HOST="${REDIS_HOST:-redis}"
REDIS_PORT="${REDIS_PORT:-6379}"

# Redis 살아날 때까지 대기
echo "⏳ waiting for redis at $REDIS_HOST:$REDIS_PORT ..."
until redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping | grep -q PONG; do
  sleep 1
done

ISSUANCE_END_TIME="$(date -v +1d '+%Y-%m-%d %H:%M:%S' 2>/dev/null || date -d '+1 day' '+%Y-%m-%d %H:%M:%S')"
ISSUANCE_START_TIME="$(date -v -1d '+%Y-%m-%d %H:%M:%S' 2>/dev/null || date -d '-1 day' '+%Y-%m-%d %H:%M:%S')"

echo "➡️  HSET coupon:1:meta ..."
redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" \
  HSET coupon:1:meta \
    coupon_id 1 \
    discount_price 1000 \
    issuance_end_time "$ISSUANCE_END_TIME" \
    issuance_start_time "$ISSUANCE_START_TIME" \
    reg_date "2025-06-30 13:00:00" \
    remaining_coupon_amount 100000 \
    total_coupon_amount 100000 \
    coupon_status "issuing" \
    coupon_name "복귀 쿠폰"

echo "✅ Redis seed done."