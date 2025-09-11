-- KEYS[1] = 쿠폰 메타 해시 키 (coupon:#couponId:meta)
-- KEYS[2] = 발급 요청 Sets 키 (coupon:#couponId:queue)
-- KEYS[3] = Streams 키 (coupon:queue:issue:job)
-- ARGV[1] = 발급 요청 couponId
-- ARGV[2] = userId
-- ARGV[3] = 발급 요청 Sets TTL Seconds

local metaKey = KEYS[1]
local setsKey = KEYS[2]
local streamsKey = KEYS[3]

local couponId = ARGV[1]
local userId = ARGV[2]
local setsTTLSeconds = ARGV[3]

-- 유효성 검증
local remain = redis.call('HGET', metaKey, 'remaining_coupon_amount')
local total = redis.call('HGET', metaKey, 'total_coupon_amount')
local requestCount = redis.call('SCARD', setsKey)

-- remain과 total이 nil일 경우 0으로 처리하는 방어 코드 추가
if remain == nil or total == nil then
    return 1 -- 쿠폰 메타 정보가 없으므로 소진으로 간주
end

-- nil 체크 후 tonumber 변환
remain = tonumber(remain)
total = tonumber(total)
requestCount = tonumber(requestCount)

-- 잔여 수량 부족 또는 총 수량 초과
if remain <= 0 or requestCount >= total then
    return 3 -- 쿠폰 소진
end
-- ...

-- Sets 존재 여부 확인 (TTL 설정용)
local setExists = redis.call('EXISTS', setsKey)

-- 중복 요청 방지 (SADD)
-- 중복된 요청이면 SADD는 0을 반환
if redis.call('SADD', setsKey, couponId .. ':' .. userId) == 0 then
    return 2 -- 중복 요청
end

-- Sets가 새로 생성되었으면 TTL 설정
if setExists == 0 then
    redis.call('EXPIRE', setsKey, tonumber(setsTTLSeconds))
end

-- Streams에 요청 추가 (XADD)
local xaddResult= redis.call('XADD', streamsKey, '*', 'couponId', couponId, 'userId', userId)

-- Streams 데이터 입력 실패 시 Sets SADD 롤백
if not xaddResult then
    redis.call('SREM', setsKey, couponId .. ':' .. userId)
    return 4 --Streams 등록 실패
end

return 1 -- 성공