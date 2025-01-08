package com.u012e.session_auth_db.service.seeder;

import com.u012e.session_auth_db.model.Dependency;
import com.u012e.session_auth_db.repository.DependencyRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DependencySeeder implements Seeder<Dependency> {
    private final Random random;
    private final SubjectRepository subjectRepository;
    private final DependencyRepository dependencyRepository;

    private final int MIN_DEPENDENCIES = 0;
    private final int MAX_DEPENDENCIES = 3;


    @Override
    public void seed(int count) {
        // WARN: count is not used :)
        var subjectCount = subjectRepository.count();
        var dependencies = new ArrayList<Dependency>(count);

        // Add dependencies for all subjects
        for (int i = 1; i <= subjectCount - MAX_DEPENDENCIES; ++i) {

            // Get a random number of dependencies
            var dependencyCount = random.nextInt(MIN_DEPENDENCIES, MAX_DEPENDENCIES);
            var dependencyIds = random
                    .longs(i + 1, subjectCount)
                    .distinct()
                    .limit(dependencyCount)
                    .toArray();
            for (long dependencyId : dependencyIds) {
                var dependency = Dependency.builder()
                        .subjectId((long) i)
                        .requiredSubjectId((long) dependencyId)
                        .build();
                dependencies.add(dependency);
            }
        }

        dependencyRepository.saveAll(dependencies);
    }
}
