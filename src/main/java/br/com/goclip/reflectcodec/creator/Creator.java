package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.UndefinedSubtypeNameException;
import lombok.Data;
import lombok.With;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@With
public class Creator {

    public static Creator create() {
        return new Creator(null, null,  null, null, null, null);
    }

    public final Class<?> type;
    public final Class<?> concreteType;
    public final String typeKeyId;
    private final Constructor<?> constructor;
    public final Parameters parameters;
    public final Map<String, Creator> subtypes;

    public Creator(Class<?> type,
                   Class<?> concreteType,
                   String typeKeyId,
                   Constructor<?> constructor,
                   Parameters parameters,
                   Map<String, Creator> subtypes) {
        this.type = type;
        this.concreteType = concreteType;
        this.typeKeyId = typeKeyId;
        this.constructor = constructor;
        this.parameters = parameters;
        this.subtypes = subtypes;
    }

    public Object newInstance(Parameters parameter) {
        try {
            if (constructor == null) {
                String propertyName = parameter.getTypeKey(this.typeKeyId);
                if (!propertyName.isBlank()) {
                    Creator creator = subtypes.get(propertyName);
                    return creator.constructor.newInstance(creator.mergeParameters(parameter).sortedValues());
                }
                throw new UndefinedSubtypeNameException(String.format("Not defined subtype name to some %s subclass", type.getSimpleName()));
            } else {
                return constructor.newInstance(parameters.sortedValues());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
        }
    }

    private Parameters mergeParameters(Parameters parameter) {
        List<CreatorParameter> collect = this.parameters.getIndexedParameters().values().stream()
                .map(creatorParameter -> parameter.getIndexedParameters().get(creatorParameter.name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new Parameters(parameter.getTypeName(), collect);
    }

    public Parameters parameters() {
        if (this.constructor == null) {
            Map<String, CreatorParameter> collect = subtypes.values().stream()
                    .map(creator -> creator.parameters.getIndexedParameters().values())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(c -> c.name, Function.identity(), (existing, replacement) -> replacement));
            return new Parameters(this.type.getSimpleName(), collect);
        } else {
            return parameters.copyOf();
        }
    }
}
