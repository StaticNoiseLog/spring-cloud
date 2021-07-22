package com.example.demo;

import com.example.demo.cat.Cat;
import com.example.demo.cat.CatRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is a unit test that leverages the Spring framework test runner. We configure it to also interact
 * nicely with the Spring Boot testing apparatus, standing up a mock web application.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DemoApplicationTests {

    @Autowired
    private MockMvc mvc; // Inject a Spring MVC test MockMvc client with which we make calls to the REST endpoints.
    @Autowired
    private CatRepository catRepository;

    @Before
    public void before() {
        Stream.of("Felix", "Garfield", "Whiskers")
                .forEach(n -> catRepository.save(new Cat(n)));
    }

    @Test
    public void catsReflectedInRead() throws Exception {
        final MediaType halJson = MediaType.parseMediaType("application/hal+json;charset=UTF-8");
        this.mvc.perform(get("/cats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(
                        mvcResult -> {
                            String contentAsString = mvcResult.getResponse().getContentAsString();
                            assertEquals("3", contentAsString
                                    .split("totalElements")[1]
                                    .split(":")[1].trim()
                                    .split(",")[0]);
                        });
    }
}
