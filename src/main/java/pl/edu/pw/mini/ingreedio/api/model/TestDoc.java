package pl.edu.pw.mini.ingreedio.api.model;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

//TODO: REMOVE
@Data
@Builder
@RequiredArgsConstructor(onConstructor_ = {@PersistenceCreator})
@Document
public class TestDoc {
    @Id
    private final String id;
    private final Date creationDate;
    private final List<String> names;
}
