package com.u012e.session_auth_db;

import com.u012e.session_auth_db.model.Subject;
import com.u012e.session_auth_db.service.DependencyService;
import com.u012e.session_auth_db.service.registration.DependencyExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DependencyExtractorTest {
    @Mock
    private DependencyService dependencyService;

    @InjectMocks
    private DependencyExtractor dependencyExtractor;

    @Test
    void TestExtractEmptySubject() {
        var subject = new Subject();
        when(dependencyService.getDependencies(subject)).thenReturn(new HashSet<>());
        var subjects = dependencyExtractor.getDependantSubjectsRecursively(subject);
        assertTrue(subjects.isEmpty());
    }

    @Test
    void TestExtractSimpleSubjects() {
        var a = Subject.builder()
                .name("A")
                .build();
        var b = Subject.builder()
                .name("B")
                .build();

        var c = Subject.builder()
                .name("C")
                .build();

        when(dependencyService.getDependencies(c)).thenReturn(Set.of(a, b));
        var subjects = dependencyExtractor.getDependantSubjectsRecursively(c);

        var expected = Set.of(a, b);

        assertEquals(expected, subjects, "Extracted subjects are not as expected");
    }

    @Test
    void TestExtractNestedSubjects() {
        var sub1 = Subject.builder()
                .name("1")
                .build();
        var sub2 = Subject.builder()
                .name("2")
                .build();
        var sub3 = Subject.builder()
                .name("3")
                .build();
        var root = Subject.builder()
                .name("4")
                .build();

        when(dependencyService.getDependencies(sub1)).thenReturn(null);
        when(dependencyService.getDependencies(sub2)).thenReturn(null);
        when(dependencyService.getDependencies(sub3)).thenReturn(Set.of(sub1, sub2));
        when(dependencyService.getDependencies(root)).thenReturn(Set.of(sub1, sub3));

        var expected = Set.of(sub1, sub2, sub3);
        var actual = dependencyExtractor.getDependantSubjectsRecursively(root);

        assertEquals(expected, actual, "Extracted subjects are not as expected");


    }
}
