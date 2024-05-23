package pl.edu.pw.mini.ingreedio.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserNotFoundException;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.user.dto.UserDto;
import pl.edu.pw.mini.ingreedio.api.user.mapper.UserDtoMapper;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final UserDtoMapper userDtoMapper;

    @Operation(security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping
    public ResponseEntity<UserDto> getUserInfo(Authentication authentication,
                                               @RequestParam Optional<String> username) {
        if (authentication.getAuthorities().stream() // TODO: change this in S5
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_MODERATOR"))) {
            String user = username.orElseGet(authentication::getName);
            return userService.getUserByUsername(user)
                .map(userDtoMapper)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(user));
        }

        if (username.isPresent() && !username.get().equals(authentication.getName())) {
            throw new UserNotFoundException(username.get());
        }

        String user = authentication.getName();
        return userService.getUserByUsername(user)
            .map(userDtoMapper)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto request) {
        try {
            return ResponseEntity.ok(userDtoMapper.apply(authService.register(request)));
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }

    @Operation(security = {@SecurityRequirement(name = "Bearer Authentication")})
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')") // TODO: change this in S5
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
            .map(userDtoMapper)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(security = {@SecurityRequirement(name = "Bearer Authentication")})
    @GetMapping("/ratings")
    public ResponseEntity<List<ReviewDto>> getUserRatings() {
        Optional<User> userOptional = userService
            .getUserByUsername(authService.getCurrentUsername());
        return userOptional.map(user -> ResponseEntity.ok(userService.getUserRatings(user)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
