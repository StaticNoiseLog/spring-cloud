package com.example.demo;

import com.example.demo.cat.Cat;
import com.example.demo.cat.CatRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.stream.Stream;

/**
 * This is a unit test that leverages the Spring framework test runner.
 * <p>
 * We don't load a real HTTP server, just have Spring mock one (SpringBootTest.WebEnvironment.MOCK). This is good enough
 * for unit tests (but not for integration tests where you want RANDOM_PORT or DEFINED_PORT).
 */
@DataJpaTest
//@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = CatRepository.class))
public class RepositoryTests {

    @Autowired
    private CatRepository catRepository;

    @BeforeAll
    public static void before() {
    }

    @Test
    public void readIt() {
        Stream.of("Felix", "Garfield", "Whiskers")
                .forEach(n -> catRepository.save(new Cat(n)));

        catRepository.findAll();
    }
}
