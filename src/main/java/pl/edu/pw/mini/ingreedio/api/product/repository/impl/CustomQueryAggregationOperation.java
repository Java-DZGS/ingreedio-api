package pl.edu.pw.mini.ingreedio.api.product.repository.impl;

import java.util.List;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class CustomQueryAggregationOperation implements AggregationOperation {
    private final String jsonOperation;

    public CustomQueryAggregationOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }

    @Override
    public Document toDocument(AggregationOperationContext aggregationOperationContext) {
        return aggregationOperationContext.getMappedObject(Document.parse(jsonOperation));
    }

    @Override
    public List<Document> toPipelineStages(AggregationOperationContext context) {
        return AggregationOperation.super.toPipelineStages(context);
    }
}