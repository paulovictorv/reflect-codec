package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.exception.AttributeNotMapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Container type wrapping around a collection of CreatorParameters. This should be the default entry point to
 * assign values to and from CreatorParameters.
 */
@With
@Getter
@AllArgsConstructor
public class Parameters {

    private final String typeName;
    private final Map<String, CreatorParameter> indexedParameters;

    public Parameters(String name, List<CreatorParameter> parameters) {
        this.typeName = name;
        this.indexedParameters = parameters.stream()
                .collect(Collectors.toMap(p -> p.name, Function.identity()));
    }

    /**
     * Gets a TypeKey. It defines which implementation to resolve to when instantiating objects via polymorphism
     *
     * @param parameterName the parameter's name identifying this type key
     * @return the value of the TypeKey
     */
    public String getTypeKey(String parameterName) {
        return String.valueOf(this.indexedParameters.get(parameterName).value());
    }

    /**
     * Assigns a value to a parameter, using its name as an identifier.
     * @param parameterName the parameter's name
     * @param computingFunction function to be applied when a match is found, resulting in the assigning of the value
     */
    public void assignValue(String parameterName, Function<CreatorParameter, Object> computingFunction) {
            var result = indexedParameters.computeIfPresent(parameterName,
                    (key, creatorParameter) -> creatorParameter.withValue(computingFunction.apply(creatorParameter))
            );
            if (result == null) {
                throw new AttributeNotMapped(this.typeName, parameterName);
            }
    }

    /**
     * Sorts the parameters using its constructor argument list order
     * @return an array of Objects ready to be passed to newInstance()
     */
    public Object[] sortedValues() {
        Object[] objects = indexedParameters.values()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.position))
                .map(CreatorParameter::value)
                .collect(Collectors.toList())
                .toArray(new Object[]{});
        return objects;
    }

    /**
     * Copies this instance, and ensures that the underlying references gets reassigned
     * @return a copy of this instance
     */
    protected Parameters copyOf() {
        HashMap<String, CreatorParameter> objectObjectHashMap = new HashMap<>();
        this.indexedParameters.entrySet().forEach(es -> {
            CreatorParameter cp = es.getValue();
            objectObjectHashMap.put(es.getKey(), cp.withValue(null));
        });
        return new Parameters(this.typeName, objectObjectHashMap);
    }
}
