package com.u012e.session_auth_db.service.registration;

import com.u012e.session_auth_db.configuration.CacheConfiguration;
import com.u012e.session_auth_db.model.Course;
import com.u012e.session_auth_db.model.Student;
import com.u012e.session_auth_db.utils.BloomFilterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("cache")
public class CacheDependencyChecker implements DependencyChecker{
    private final DatabaseDependencyChecker databaseDependencyChecker;
    private String getValue (Student student, Course course){
        return String.format("%s:%d:%d",
                CacheConfiguration.DEPENDENCY_CACHE,
                student.getId(),
                course.getId());
    }
    @Override
    public boolean checkDependency(Student student, Course course){
        String value = getValue(student, course);
        if (!BloomFilterManager.main.mightContain(value)){
            return false;
        }
        if (!BloomFilterManager.backup.mightContain(value)){
            return true;
        }
        return databaseDependencyChecker.checkDependency(student, course);
    }
}
