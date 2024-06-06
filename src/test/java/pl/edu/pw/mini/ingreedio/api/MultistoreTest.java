package pl.edu.pw.mini.ingreedio.api;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.repository.support.Repositories;
import pl.edu.pw.mini.ingreedio.api.product.model.ProductDocument;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

public class MultistoreTest extends IntegrationTest {
    @Autowired
    ApplicationContext context;

    @Test
    void givenMultipleStores_whenCreatingRepositories_thenTheyAreAssignedToAppropriateStores() {
        // Given

        // When
        var repositories = new Repositories(context);

        // Then
        assertInstanceOf(JpaEntityInformation.class,
            repositories.getEntityInformationFor(User.class));
        assertInstanceOf(MongoEntityInformation.class,
            repositories.getEntityInformationFor(ProductDocument.class));
    }
}
