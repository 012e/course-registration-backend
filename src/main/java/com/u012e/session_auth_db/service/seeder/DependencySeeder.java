package com.u012e.session_auth_db.service.seeder;

import com.u012e.session_auth_db.model.Dependency;
import com.u012e.session_auth_db.repository.DependencyRepository;
import com.u012e.session_auth_db.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DependencySeeder implements Seeder<Dependency> {
    private final Random random;
    private final SubjectRepository subjectRepository;
    private final DependencyRepository dependencyRepository;

    private final int MIN_DEPENDENCIES = 0;
    private final int MAX_DEPENDENCIES = 5;


    @Override
    public void seed(int count) {
        // WARN: count is not used :)
        var subjectCount = subjectRepository.count();
        var dependencies = new ArrayList<Pair<Long, Long>>(count);

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
                dependencies.add(Pair.of((long) i, dependencyId));
            }
        }

        dependencyRepository.addAllByIds(dependencies);
    }
}
