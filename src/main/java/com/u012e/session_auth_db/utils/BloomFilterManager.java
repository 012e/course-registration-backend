package com.u012e.session_auth_db.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;

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
    public static String getValue (Student student, Course course){
        return String.format("%s:%d:%d",
                CacheConfiguration.DEPENDENCY_CACHE,
                student.getId(),
                course.getId());
    }
}
