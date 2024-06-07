package pl.edu.pw.mini.ingreedio.api.common.mapping;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;

@RequiredArgsConstructor
public class NullableConverter<S, D> extends AbstractConverter<S, D> {
    final Function<S, D> converter;

    @Override
    protected D convert(S source) {
        return source == null ? null : converter.apply(source);
    }
}
