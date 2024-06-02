package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

public class UserServiceTest extends IntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    public void givenValidUserData_whenCreateUser_thenSuccess() {
        // Given
        String displayName = "Test13";
        String email = "test@test.pl";

        // When
        User user = userService.createUser(displayName, email);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getDisplayName()).isEqualTo(displayName);
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    public void givenDuplicatedEmail_whenCreateUser_thenExceptionThrown() {
        // Given
        String displayName = "Test13";
        String email = "user@us.com";

        // When
        Exception problem = catchException(() -> userService.createUser(displayName, email));

        // Then
        assertThat(problem).isInstanceOf(UserAlreadyExistsException.class);
    }
}
