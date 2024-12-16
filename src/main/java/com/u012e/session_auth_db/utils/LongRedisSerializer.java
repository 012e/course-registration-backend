package com.u012e.session_auth_db.utils;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.ByteBuffer;

public class LongRedisSerializer implements RedisSerializer<Long> {
    @Override
    public byte[] serialize(Long value) throws SerializationException {
        if (value == null) {
            return new byte[0]; // Return an empty byte array for null values
        }
        try {
            return ByteBuffer.allocate(Long.BYTES)
                    .putLong(value)
                    .array();
        } catch (Exception e) {
            throw new SerializationException("Error serializing Long", e);
        }
    }

    @Override
    public Long deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null; // Return null for empty byte arrays
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            return buffer.getLong();
        } catch (Exception e) {
            throw new SerializationException("Error deserializing Long", e);
        }
    }
}