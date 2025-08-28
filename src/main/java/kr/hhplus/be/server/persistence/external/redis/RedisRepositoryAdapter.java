package kr.hhplus.be.server.persistence.external.redis;

import kr.hhplus.be.server.application.redis.repository.RedisRepository;
import kr.hhplus.be.server.common.redis.RedisKeys;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@Component
public class RedisRepositoryAdapter implements RedisRepository {
    private final RedissonClient redissonClient;

    public RedisRepositoryAdapter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RStream<String, String> initConsumerGroup(String groupName){
        RStream<String, String> queueStream = redissonClient.getStream(RedisKeys.COUPON_ISSUE_JOB.format());

        try {
            // 그룹 없으면 생성 (already exists 예외는 무시)
            queueStream.createGroup(StreamCreateGroupArgs.name(groupName).makeStream());
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().contains("BUSYGROUP")) {
                throw e;
            }
        }

        return queueStream;
    }

    @Override
    public RMap<String, String> getHashes(String key, Codec codec) {
        return redissonClient.getMap(key, codec);
    }

    private String loadLua(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            String luaScript = "";
            if(is != null){
                luaScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            return luaScript;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long decStockFromRedis(String couponId){
        try {
            return redissonClient.getScript().eval(
                    RScript.Mode.READ_WRITE,
                    loadLua("lua/decStock.lua"),// Lua 스크립트 내용
                    RScript.ReturnType.INTEGER,// 반환 타입
                    Collections.singletonList("coupon:"+couponId+":meta"),// KEYS[1]
                    "remain_quantity",// ARGV[1]
                    1);
        }catch (Exception e){
            return 0L;
        }
    }

    @Override
    public Long incStockFromRedis(String couponId){
        try {
            return redissonClient.getScript().eval(
                    RScript.Mode.READ_WRITE,
                    loadLua("lua/incStock.lua"),// Lua 스크립트 내용
                    RScript.ReturnType.INTEGER,// 반환 타입
                    Collections.singletonList("coupon:"+couponId+":meta"),// KEYS[1]
                    "remain_quantity",// ARGV[1]
                    1);
        }catch (Exception e){
            return 0L;
        }
    }

    @Override
    public Long requestCouponIssue(String hashesKey, String setsKey, String streamsKey, String couponId, String userId, String setsTTLSeconds){
        try {
            // Lua 스크립트 내용
            return redissonClient.getScript(StringCodec.INSTANCE).eval(
                    RScript.Mode.READ_WRITE,
                    loadLua("lua/requestCouponIssue.lua"),// Lua 스크립트 내용
                    RScript.ReturnType.INTEGER,
                    Arrays.asList(hashesKey, setsKey, streamsKey),
                    couponId,
                    userId,
                    setsTTLSeconds
            );
        }catch (Exception e){
            return 0L;
        }
    }
}
