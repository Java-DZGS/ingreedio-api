package pl.edu.pw.mini.ingreedio.api.common.mapping;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

@RequiredArgsConstructor
public class BuilderConverter<S, B, D> implements Converter<S, D> {
    final Function<B, D> build;
    final Class<B> builder;

    @Override
    public D convert(MappingContext<S, D> context) {
        var mapContext = context.create(context.getSource(), builder);
        return build.apply(context.getMappingEngine().map(mapContext));
    }
}
