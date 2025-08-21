package kr.hhplus.be.server.common.redis;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

@Component
public class LuaScript {

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

    public Long decStockFromRedis(RedissonClient redissonClient,String couponId){
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

    public Long incStockFromRedis(RedissonClient redissonClient,String couponId){
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

    public Long requestCouponIssue(RedissonClient redissonClient, String hashesKey, String setsKey, String streamsKey, String couponId, String userId, String setsTTLSeconds){
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
