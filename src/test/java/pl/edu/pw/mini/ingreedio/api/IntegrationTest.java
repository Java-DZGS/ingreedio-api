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

    private static final String[] MONGO_NAME = new String[] {"mongo1_1", "mongo2_1", "mongo3_1"};
    public static final int MONGO_PORT = 27017;
    private static final String MONGO_DB = "ingreedio";
    private static final String MONGO_OPTIONS = "?replicaSet=rs0";
    private static final String MONGO_URI = "mongodb://%s/%s";

    @SuppressWarnings("rawtypes")
    public static DockerComposeContainer environment =
        new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService(POSTGRES_NAME, POSTGRES_PORT,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService(MONGO_NAME[0], MONGO_PORT,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService(MONGO_NAME[1], MONGO_PORT + 1,
                Wait.forListeningPort().withStartupTimeout(
                    Duration.ofSeconds(30)))
            .withExposedService(MONGO_NAME[2], MONGO_PORT + 2,
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
        var host = environment.getServiceHost(MONGO_NAME[0], MONGO_PORT);
        var port = environment.getServicePort(MONGO_NAME[0], MONGO_PORT);
        var connectionBuilder = new StringBuilder(host).append(':').append(port);

        for (int i = 1; i < 3; i++) {
            host = environment.getServiceHost(MONGO_NAME[i], MONGO_PORT + i);
            port = environment.getServicePort(MONGO_NAME[i], MONGO_PORT + i);
            connectionBuilder.append(',').append(host).append(':').append(port);
        }

        String connection = String.format(MONGO_URI, connectionBuilder, MONGO_OPTIONS);
        System.out.println(connection);
        registry.add("spring.data.mongodb.uri", () -> connection);
        registry.add("spring.data.mongodb.database", () -> MONGO_DB);
    }
}
