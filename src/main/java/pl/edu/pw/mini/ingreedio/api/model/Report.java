package pl.edu.pw.mini.ingreedio.api.model;

public class Report {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String content;

    public Report(Long id, Long reviewId, Long userId, String content) {
        this.id = id;
        this.reviewId = reviewId;
        this.userId = userId;
        this.content = content;
    }
}
