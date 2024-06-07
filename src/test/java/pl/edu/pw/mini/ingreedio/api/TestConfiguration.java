package pl.edu.pw.mini.ingreedio.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Configuration
public class TestConfiguration {
    @Autowired
    private UserService userService;

    @Bean
    @Primary
    public User defaultUser() {
        return userService.getUserByUsername("user");
    }

    @Bean
    public User moderatorUser() {
        return userService.getUserByUsername("mod");
    }
}
