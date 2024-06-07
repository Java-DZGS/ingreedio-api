package pl.edu.pw.mini.ingreedio.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.ingredient.service.IngredientService;
import pl.edu.pw.mini.ingreedio.api.review.dto.ReviewDto;
import pl.edu.pw.mini.ingreedio.api.review.mapper.ReviewDtoMapper;
import pl.edu.pw.mini.ingreedio.api.user.dto.UserDto;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
@Transactional
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get user data",
        description = "Fetches user information based on authentication.",
        security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<UserDto> getUserInfo(Authentication authentication) {
        User user = userService.getUser(authentication);
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @Operation(summary = "Get user data by username",
        description = "Fetches user information based on provided username. "
                      + "Available for moderators.",
        security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('GET_USER_INFO')")
    @GetMapping("/search")
    public ResponseEntity<UserDto> getAnyUserInfo(@RequestParam String username) {
        return ResponseEntity.ok(modelMapper.map(userService.getUserByUsername(username),
            UserDto.class));
    }

    @Operation(summary = "Register a new user",
        description = "Registers a new user with the provided details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDto request) {
        User user = userService.createUser(request.displayName(), request.email());
        authService.register(request.username(), request.password(), user);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get user by ID",
        description = "Fetches user information based on the provided user ID.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('GET_USER_INFO')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(modelMapper.map(userService.getUserById(id), UserDto.class));
    }


    private final ReviewDtoMapper reviewDtoMapper; //TODO: temp

    @Operation(summary = "Get user ratings",
        description = "Fetches the ratings/reviews associated with logged in user.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User ratings retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class))
            )),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getUserReviews(Authentication authentication) {
        User user = userService.getUser(authentication);
        return ResponseEntity.ok(userService.getUserReviews(user).stream()
            .map(review -> reviewDtoMapper.apply(review, user))
            .toList()); // TODO: mapper
    }

    private final IngredientService ingredientService; //TODO: temp

    @Operation(summary = "Get liked ingredients",
        description = "Fetches a list of ingredients liked by the user.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liked ingredients retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))
            ))
    })
    @GetMapping("/liked-ingredients")
    public ResponseEntity<List<IngredientDto>> getLikedIngredients(Authentication authentication) {
        List<IngredientDto> ingredientDtos = userService.getUser(authentication)
            .getLikedIngredients()
            .stream()
            .map((ingredient) -> modelMapper.map(ingredient, IngredientDto.class))
            .toList();
        return ResponseEntity.ok(ingredientDtos);
    }

    @Operation(summary = "Get allergens",
        description = "Fetches a list of ingredients that are classified as allergens.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Allergens retrieved successfully",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = IngredientDto.class))))
    })
    @GetMapping("/allergens")
    public ResponseEntity<List<IngredientDto>> getAllergens(Authentication authentication) {
        List<IngredientDto> ingredientDtos = userService.getUser(authentication)
            .getAllergens()
            .stream()
            .map((ingredient) -> modelMapper.map(ingredient, IngredientDto.class))
            .toList();
        return ResponseEntity.ok(ingredientDtos);
    }
}
