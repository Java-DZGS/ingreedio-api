package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Report {
    private final Integer id;
    private final Integer reviewId;
    private final Integer userId;
    private final String content;

    public Report(Integer id, Integer reviewId, Integer userId, String content) {
        this.id = id;
        this.reviewId = reviewId;
        this.userId = userId;
        this.content = content;
    }
}
