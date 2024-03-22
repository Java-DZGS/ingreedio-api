package pl.edu.pw.mini.ingreedio.api.model;

public class Review {
    private final Long id;
    private final Long userId;
    private final Long productId;
    private final Integer rating;
    private final String content;
    private final Integer likesCount;
    private final Integer dislikesCount;
    private final Integer reportsCount;

    public Review(Long id,
                  Long userId,
                  Long productId,
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
