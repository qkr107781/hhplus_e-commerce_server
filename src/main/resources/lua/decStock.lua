-- KEYS[1] = 쿠폰 해시 key
-- ARGV[1] = remain field
-- ARGV[2] = 차감 수량
local remain = tonumber(redis.call('HGET', KEYS[1], ARGV[1]))
if not remain then
    return -1
end
if remain <= 0 then
    return 0
end
redis.call('HINCRBY', KEYS[1], ARGV[1], -tonumber(ARGV[2]))
return 1