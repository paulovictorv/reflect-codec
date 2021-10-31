package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.exception.IncompatibleTypesException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that models a constructor's arguments. It's immutable, and can have values assigned to it.
 *
 * It is based on this class that the codec knows how to correctly invoke a given class constructor.
 */
@With
@AllArgsConstructor
public class CreatorParameter implements Comparable<CreatorParameter> {

    /**
     * Creates a CreatorParameter. It transparently treats generics.
     *
     * @param parameter the constructor parameter extracted via reflection
     * @param position the position it was found in
     * @param parameterName the name associated with this parameter
     * @return a new instance of CreatorParameter
     */
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

    private static CreatorParameter createDefault(int position, Class<?> type, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), null, name, null,  PrimitiveUtils.defaultValue(type));
    }

    private static CreatorParameter createGeneric(int position, Class<?> type, Class<?> genericType, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), genericType, name, null,  PrimitiveUtils.defaultValue(type));
    }

    private static CreatorParameter createSubTypes(int position, Class<?> type, List<Class<?>> subTypes, String name) {
        return new CreatorParameter(position, PrimitiveUtils.mapToBoxed(type), null, name, subTypes,  PrimitiveUtils.defaultValue(type));
    }

    public final int position;
    public final Class<?> type;
    public final Class<?> genericType;
    public final String name;
    public final List<Class<?>> subTypes;
    @With(AccessLevel.NONE)
    private final Object value;

    /**
     * Maps this parameter with a value.
     * @param value the value being assigned
     * @return a new instance of CreatorParameter, with the value set
     */
    public CreatorParameter withValue(Object value) {
        if (value != null && !type.isInstance(value)) {
            throw new IncompatibleTypesException(type, value.getClass());
        } else {
            return new CreatorParameter(position, type, genericType, name, subTypes, value);
        }
    }

    /**
     * Extracts this parameter value, trying to prevent nullability issues with primitive types.
     *
     * @return the value
     */
    public Object value() {
        if (value == null && type.isPrimitive()) {
            //TODO remove special treatment for primitive values
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
