package pl.edu.pw.mini.ingreedio.api.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ModelPatcher<T> {

    public T patch(T target, T patch) {
        return patchAndExcludeFields(target, patch, Set.of());
    }

    public T patchAndExcludeFields(T target, T patch, Set<String> excludeFields) {
        for (Field field : patch.getClass().getDeclaredFields()) {
            if (excludeFields.contains(field.getName())
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object newValue = field.get(patch);
                if (newValue != null) {
                    field.set(target, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return target;
    }
}
