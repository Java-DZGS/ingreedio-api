package pl.edu.pw.mini.ingreedio.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@Configuration
@Transactional
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
