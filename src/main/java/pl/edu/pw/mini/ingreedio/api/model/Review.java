package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@RequiredArgsConstructor
@Getter
@Setter
public class Review {
    private final Integer id;
    private final Integer userId;
    private final Integer productId;
    private final Integer rating;
    private final String content;
    private final Integer likesCount;
    private final Integer dislikesCount;
    private final Integer reportsCount;
}
