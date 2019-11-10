package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.IncompatibleTypesException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.With;

@Data
@With
public class CreatorParameter implements Comparable<CreatorParameter> {

    public static CreatorParameter create(int position, Class<?> type, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), null, name, PrimitiveUtils.defaultValue(type));
    }

    public static CreatorParameter createGeneric(int position, Class<?> type, Class<?> genericType, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), genericType, name, PrimitiveUtils.defaultValue(type));
    }

    public final int position;
    public final Class<?> type;
    public final Class<?> genericType;
    public final String name;
    @With(AccessLevel.NONE)
    private final Object value;

    public CreatorParameter withValue(Object value) {
        if (!type.isInstance(value)) {
            throw new IncompatibleTypesException(type, value.getClass());
        } else {
            return new CreatorParameter(position, type, null, name, value);
        }
    }

    public Object value() {
        if (value == null && type.isPrimitive()) {
            return PrimitiveUtils.defaultValue(type);
        } else {
            return value;
        }
    }

    @Override
    public int compareTo(CreatorParameter o) {
        return Integer.compare(this.position, o.position);
    }
}
