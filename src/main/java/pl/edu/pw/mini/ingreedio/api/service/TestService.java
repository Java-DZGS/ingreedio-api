package pl.edu.pw.mini.ingreedio.api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.TestDoc;
import pl.edu.pw.mini.ingreedio.api.repository.TestRepository;

@Service
public class TestService {
    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public void saveDoc(TestDoc testDoc) {
        testRepository.save(testDoc);
    }

    public List<TestDoc> getAllTests() {
        return testRepository.findAll();
    }

    public TestDoc getTestById(String id) {
        return testRepository.findById(id).orElse(null);
    }

    public void clearTests() {
        testRepository.deleteAll();
    }
}
