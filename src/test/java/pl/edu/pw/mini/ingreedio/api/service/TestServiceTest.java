package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.model.TestDoc;

//TODO: REMOVE
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestServiceTest extends IntegrationTest {
    @Autowired
    private TestService testService;

    @Test
    @Order(0)
    public void givenDatabase_whenClearing_thenSuccess() {
        // Given
        // When
        testService.clearTests();
        // Then
    }

    @Test
    @Order(1)
    public void givenValidTestObject_whenSaving_thenSaved() {
        // Given
        TestDoc doc = TestDoc.builder()
            .creationDate(new Date())
            .names(List.of("Ala", "Ela"))
            .build();

        // When
        testService.saveDoc(doc);
        List<TestDoc> saved = testService.getAllTests();

        // Then
        assertEquals(1, saved.size());
    }

    @Test
    @Order(2)
    public void givenOtherTestObject_whenSaving_thenSaved() {
        // Given
        TestDoc doc = TestDoc.builder()
            .creationDate(new Date())
            .names(List.of("Jola", "Pola"))
            .build();

        // When
        testService.saveDoc(doc);
        List<TestDoc> saved = testService.getAllTests();

        // Then
        assertEquals(2, saved.size());
    }
}
