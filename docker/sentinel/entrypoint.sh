a#!/bin/sh

 sed -i "s/\$SENTINEL_QUORUM/$SENTINEL_QUORUM/g" /etc/redis/sentinel.conf
 sed -i "s/\$SENTINEL_DOWN_AFTER/$SENTINEL_DOWN_AFTER_MS/g" /etc/redis/sentinel.conf
 sed -i "s/\$SENTINEL_FAILOVER/$SENTINEL_FAILOVER_TIMEOUT/g" /etc/redis/sentinel.conf

 exec docker-entrypoint.sh redis-server /etc/redis/sentinel.conf --sentinel