package kr.hhplus.be.server.application.redis.repository;

import org.redisson.api.RMap;
import org.redisson.api.RStream;
import org.redisson.client.codec.Codec;

public interface RedisRepository {

    RStream<String, String> initConsumerGroup(String groupName);

    RMap<String, String> getHashes(String key, Codec codec);

    Long decStockFromRedis(String couponId);

    Long incStockFromRedis(String couponId);

    Long requestCouponIssue(String hashesKey, String setsKey, String streamsKey, String couponId, String userId, String setsTTLSeconds);


}
