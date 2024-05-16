package pl.edu.pw.mini.ingreedio.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Transactional
public class IntegrationTest {
    private static final String POSTGRES_NAME = "postgres:13.1-alpine";
    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_JDBC = "jdbc:postgresql://%s:%s/%s";
    private static final String POSTGRES_DB = "ingreedio";
    private static final String POSTGRES_USERNAME = "compose-postgres";
    private static final String POSTGRES_PASSWORD = "compose-postgres";

    private static final String MONGO_NAME = "mongo:latest";
    public static final int MONGO_PORT = 27017;
    private static final String MONGO_DB = "ingreedio";
    private static final String MONGO_OPTIONS = "?replicaSet=docker-rs&directConnection=true";
    private static final String MONGO_URI = "mongodb://%s:%d/%s";

    public static PostgreSQLContainer<?> postgresContainer;
    public static MongoDBContainer mongoContainer;

    @BeforeAll
    public static void setUp() {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_NAME))
            .withExposedPorts(5432)
            .withNetworkAliases("postgres")
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD)
            .withDatabaseName(POSTGRES_DB);

        mongoContainer = new MongoDBContainer(DockerImageName.parse(MONGO_NAME))
            .withNetworkAliases("mongo")
            .withExposedPorts(MONGO_PORT);

        postgresContainer.start();
        mongoContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoContainer.stop();
        postgresContainer.stop();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        // JPA -- POSTGRES
        registry.add("spring.datasource.url", () -> String.format(POSTGRES_JDBC,
            postgresContainer.getHost(),
            postgresContainer.getMappedPort(POSTGRES_PORT),
            POSTGRES_DB));
        registry.add("spring.datasource.username", () -> POSTGRES_USERNAME);
        registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);

        // MONGO
        String connection = String.format(MONGO_URI,
            mongoContainer.getHost(), mongoContainer.getMappedPort(MONGO_PORT),
            MONGO_OPTIONS);
        registry.add("spring.data.mongodb.uri", () -> connection);
        registry.add("spring.data.mongodb.database", () -> MONGO_DB);
    }
}
