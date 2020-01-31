package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.IncompatibleTypesException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.With;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@With
public class CreatorParameter implements Comparable<CreatorParameter> {

    private static CreatorParameter createDefault(int position, Class<?> type, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), null, name, null,  PrimitiveUtils.defaultValue(type));
    }

    private static CreatorParameter createGeneric(int position, Class<?> type, Class<?> genericType, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), genericType, name, null,  PrimitiveUtils.defaultValue(type));
    }

    private static CreatorParameter createSubTypes(int position, Class<?> type, List<Class<?>> subTypes, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), null, name, subTypes,  PrimitiveUtils.defaultValue(type));
    }

    public static CreatorParameter create(Parameter parameter, int position, String parameterName) {
        if (parameter.getParameterizedType() instanceof ParameterizedType) {
            Type genericType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
            return createGeneric(position, parameter.getType(), (Class<?>) genericType, parameterName);
        } else if (parameter.getType().getAnnotation(JsonSubTypes.class) != null) {
            List<Class<?>> subTypes = Arrays.stream(parameter.getType().getAnnotation(JsonSubTypes.class).value())
                    .map(JsonSubTypes.Type::value)
                    .collect(Collectors.toList());
            return createSubTypes(position, parameter.getType(), subTypes, parameterName);
        } else {
            return createDefault(position, parameter.getType(), parameterName);
        }
    }

    public final int position;
    public final Class<?> type;
    public final Class<?> genericType;
    public final String name;
    public final List<Class<?>> subTypes;
    @With(AccessLevel.NONE)
    private final Object value;

    public CreatorParameter withValue(Object value) {
        if (!type.isInstance(value)) {
            throw new IncompatibleTypesException(type, value.getClass());
        } else {
            return new CreatorParameter(position, type, genericType, name, subTypes, value);
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
