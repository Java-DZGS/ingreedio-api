package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    public Review(Integer id,
                  Integer userId,
                  Integer productId,
                  Integer rating,
                  String content,
                  Integer likesCount,
                  Integer dislikesCount,
                  Integer reportsCount) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.content = content;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.reportsCount = reportsCount;
    }
}
