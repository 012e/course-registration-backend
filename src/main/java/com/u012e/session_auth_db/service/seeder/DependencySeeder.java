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

    private int randomReversedExponential(int begin, int end, double mean) {
        if (begin >= end) {
            throw new IllegalArgumentException("End must be greater than begin.");
        }

        double lambda = 1.0 / mean;

        // Generate an exponentially distributed random value using inverse transform sampling
        double u = random.nextDouble();
        double exponentialValue = -Math.log(1 - u) / lambda;

        // Scale and reverse the value to fit within the range [begin, end], skewing towards the upper end
        int result = end - (int) Math.round(exponentialValue * (end - begin) / mean);

        // Clamp the result within the range [begin, end]
        result = Math.max(begin, Math.min(result, end));

        return result;
    }

    @Override
    public void seed(int count) {
        // WARN: count is not used :)
        var subjectCount = subjectRepository.count();
        var dependencies = new ArrayList<Pair<Long, Long>>(count);

        // Add dependencies for all subjects
        for (int i = 1; i <= subjectCount - MAX_DEPENDENCIES; ++i) {

            // Get a random number of dependencies
            var dependencyCount = randomReversedExponential(MIN_DEPENDENCIES, MAX_DEPENDENCIES, 1);
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
