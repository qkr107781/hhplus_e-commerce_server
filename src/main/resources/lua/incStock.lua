-- KEYS[1] = 쿠폰 해시 key
-- ARGV[1] = remain field
-- ARGV[2] = 복구 수량
redis.call('HINCRBY', KEYS[1], ARGV[1], tonumber(ARGV[2]))
return 1