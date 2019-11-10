package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.AttributeNotMapped;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@With
@RequiredArgsConstructor
public class Creator {

    public static Creator create() {
        return new Creator(null, null, null, null);
    }

    public final Class<?> type;
    public final Class<?> concreteType;
    public final Constructor<?> constructor;

    @With(AccessLevel.NONE)
    public final List<CreatorParameter> parameters;
    private Map<String, CreatorParameter> indexedParameters;

    private Creator(Class<?> type,
                    Class<?> concreteType,
                    Constructor<?> constructor,
                    List<CreatorParameter> parameters,
                    Map<String, CreatorParameter> indexedParameters) {
        this(type, concreteType, constructor, parameters);
        this.indexedParameters = indexedParameters;
    }

    public Object newInstance() {
        try {
            Object[] sortedParameters = indexedParameters.values()
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.position))
                    .map(CreatorParameter::value)
                    .collect(Collectors.toList())
                    .toArray(new Object[]{});

            return constructor.newInstance(sortedParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Creator withParameters(List<CreatorParameter> parameters) {
        Map<String, CreatorParameter> indexedParameters = parameters.stream()
                .collect(Collectors.toMap(p -> p.name, Function.identity()));
        return new Creator(type, concreteType, constructor, parameters, indexedParameters);
    }

    public Creator computeValue(String name, Function<CreatorParameter, Object> computingFunction) {
        return withValue(name, computingFunction.apply(getCreatorParameter(name)));
    }

    private CreatorParameter getCreatorParameter(String name) {
        CreatorParameter creatorParameter = indexedParameters.get(name);

        if (creatorParameter == null) {
            throw new AttributeNotMapped(type.getSimpleName(), name);
        } else {
            return creatorParameter;
        }
    }

    public Creator withValue(String name, Object value) {
        CreatorParameter creatorParameter = indexedParameters.get(name);

        if (creatorParameter == null) {
            throw new AttributeNotMapped(type.getSimpleName(), name);
        } else {
            indexedParameters.put(creatorParameter.name, creatorParameter.withValue(value));
            return this;
        }
    }
}
