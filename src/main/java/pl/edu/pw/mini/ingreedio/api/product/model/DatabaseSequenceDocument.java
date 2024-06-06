package pl.edu.pw.mini.ingreedio.api.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Data
@NoArgsConstructor
public class DatabaseSequenceDocument {
    @Id
    private String id;

    private Long seq;
}