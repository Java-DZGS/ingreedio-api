package pl.edu.pw.mini.ingreedio.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Data
@NoArgsConstructor
public class DatabaseSequence {
    @Id
    private String id;
    private Long seq;
}