package pl.edu.pw.mini.ingreedio.api.user.mapper;

import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.ingredient.dto.IngredientDto;
import pl.edu.pw.mini.ingreedio.api.user.dto.UserDto;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Service
@RequiredArgsConstructor
public class UserDtoMapper implements Function<User, UserDto> {
    private final ModelMapper modelMapper;

    @Override
    public UserDto apply(User user) {
        return UserDto.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .likedIngredients(user.getLikedIngredients().stream()
                .map((ingredient) -> modelMapper.map(
                    ingredient, IngredientDto.IngredientDtoBuilder.class
                ).build())
                .collect(Collectors.toSet()))
            .allergens(user.getAllergens().stream()
                .map((ingredient) -> modelMapper.map(
                    ingredient, IngredientDto.IngredientDtoBuilder.class
                ).build())
                .collect(Collectors.toSet()))
            .build();
    }
}
