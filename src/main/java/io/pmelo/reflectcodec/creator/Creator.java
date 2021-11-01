package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.exception.UndefinedSubtypeNameException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Models a Constructor declaration with its arguments. It's assembled by a CreatorFactory.
 *
 * A Creator that models an abstract class does not have an explicit Constructor. Instead, it resolves the correct
 * constructor using the "typeKey" specified in the abstract class declaration
 */
@Data
@With
@Builder
@AllArgsConstructor
public class Creator {

    public static Creator concreteCreator(Class<?> type, Constructor<?> constructor, Parameters parameters) {
        return Creator.builder()
                .type(type)
                .constructor(constructor)
                .parameters(parameters)
                .build();
    }

    public static Creator abstractCreator(Class<?> type, Map<String, Creator> subtypes, String typeKeyId) {
        return Creator.builder()
                .type(type)
                .subtypes(subtypes)
                .typeKeyId(typeKeyId)
                .build();
    }

    public final Class<?> type;
    public final String typeKeyId;
    private final Constructor<?> constructor;
    public final Parameters parameters;
    public final Map<String, Creator> subtypes;

    /**
     * Reflectively creates a new instance of type. It takes Parameters and merges with the internal Parameters.
     * @param parameters used to create a new instance
     * @return a new instance of a given type
     */
    public Object newInstance(Parameters parameters) {
        try {
            if (constructor == null) {
                String propertyName = parameters.getTypeKey(this.typeKeyId);
                if (!propertyName.isBlank()) {
                    Creator creator = subtypes.get(propertyName);
                    return creator.constructor.newInstance(creator.mergeParameters(parameters).sortedValues());
                }
                throw new UndefinedSubtypeNameException(type);
            } else {
                return constructor.newInstance(parameters.sortedValues());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
        }
    }

    /**
     * Returns the declared parameters for this given Creator
     * @return an instance of Parameters
     */
    public Parameters parameters() {
        //constructors can be null if we are navigating through abstract classes
        if (this.constructor == null) {
            //assembles a pseudo parameters collecting from all declared subtypes
            Map<String, CreatorParameter> collect = subtypes.values().stream()
                    .map(creator -> creator.parameters.getIndexedParameters().values())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(c -> c.name, Function.identity(), (existing, replacement) -> replacement));
            return new Parameters(this.type.getSimpleName(), collect);
        } else {
            return parameters.copyOf();
        }
    }

    private Parameters mergeParameters(Parameters parameter) {
        List<CreatorParameter> collect = this.parameters.getIndexedParameters().values().stream()
                .map(creatorParameter -> parameter.getIndexedParameters().get(creatorParameter.name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new Parameters(parameter.getTypeName(), collect);
    }

}
