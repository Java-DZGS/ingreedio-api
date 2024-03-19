package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Report {
    private final Integer id;
    private final Integer reviewId;
    private final Integer userId;
    private final String content;
}
