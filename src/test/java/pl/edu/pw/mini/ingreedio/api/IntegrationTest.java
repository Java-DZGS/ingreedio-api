package pl.edu.pw.mini.ingreedio.api;

import java.io.File;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@SpringBootTest
@Transactional
public class IntegrationTest {
    private static final String POSTGRES_NAME = "postgres_1";
    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_JDBC = "jdbc:postgresql://%s:%s/%s";
    private static final String POSTGRES_DB = "ingreedio";
    private static final String POSTGRES_USERNAME = "compose-postgres";
    private static final String POSTGRES_PASSWORD = "compose-postgres";

    private static final String MONGO_HOST = "localhost";
    public static final int MONGO_PORT = 30001;
    private static final String MONGO_DB = "ingreedio";
    private static final String MONGO_OPTIONS = "?replicaSet=rs0";
    private static final String MONGO_URI = "mongodb://%s/%s";

    @SuppressWarnings("rawtypes")
    public static DockerComposeContainer environment =
        new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService(POSTGRES_NAME, POSTGRES_PORT,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService("mongo1", MONGO_PORT,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService("mongo2", MONGO_PORT + 1,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService("mongo3", MONGO_PORT + 2,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)));

    @BeforeAll
    public static void setUp() {
        environment.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        // JPA -- POSTGRES
        registry.add("spring.datasource.url", () -> String.format(POSTGRES_JDBC,
            environment.getServiceHost(POSTGRES_NAME, POSTGRES_PORT),
            environment.getServicePort(POSTGRES_NAME, POSTGRES_PORT),
            POSTGRES_DB));
        registry.add("spring.datasource.username", () -> POSTGRES_USERNAME);
        registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);

        // MONGO
        var connectionBuilder = new StringBuilder(MONGO_HOST).append(':').append(MONGO_PORT);
        for (int i = 1; i < 3; i++) {
            connectionBuilder.append(',').append(MONGO_HOST).append(':').append(MONGO_PORT + i);
        }

        String connection = String.format(MONGO_URI, connectionBuilder, MONGO_OPTIONS);
        System.out.println(connection);
        registry.add("spring.data.mongodb.uri", () -> connection);
        registry.add("spring.data.mongodb.database", () -> MONGO_DB);
    }
}
