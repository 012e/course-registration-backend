package com.u012e.session_auth_db.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;

public class BloomFilterManager {
    public static final BloomFilter<String> main = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            100000,
            0.001);
    public static final BloomFilter<String> backup = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            1000,
            0.001);
}
