package com.u012e.session_auth_db.utils;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.ByteBuffer;

public class IntegerRedisSerializer implements RedisSerializer<Integer> {
    @Override
    public byte[] serialize(Integer integer) throws SerializationException {
        if (integer == null) {
            return new byte[0]; // Return an empty byte array for null values
        }
        try {
            return ByteBuffer.allocate(Integer.BYTES)
                    .putInt(integer)
                    .array();
        } catch (Exception e) {
            throw new SerializationException("Error serializing Integer", e);
        }
    }

    @Override
    public Integer deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null; // Return null for empty byte arrays
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            return buffer.getInt();
        } catch (Exception e) {
            throw new SerializationException("Error deserializing Integer", e);
        }
    }
}