package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.AttributeNotMapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@With
@AllArgsConstructor
public class Parameters {

    private Creator creator;
    private Map<String, CreatorParameter> indexedParameters;

    public Parameters(List<CreatorParameter> parameters) {
        this.indexedParameters = parameters.stream()
                .collect(Collectors.toMap(p -> p.name, Function.identity()));
    }

    private Parameters(Map<String, CreatorParameter> indexedParameters) {
        this.indexedParameters = indexedParameters;
    }

    public Parameters assignValue(String parameterName, Function<CreatorParameter, Object> computingFunction) {
        Map<String, CreatorParameter> copyOf = new HashMap<>();

        try {
            copyOf.computeIfPresent(parameterName,
                    (key, creatorParameter) -> creatorParameter.withValue(computingFunction.apply(creatorParameter)));
            return new Parameters(copyOf);
        } catch (NullPointerException e) {
            throw new AttributeNotMapped(this.creator.type.getSimpleName(), parameterName);
        }
    }

    public Object[] sortedValues() {
        return indexedParameters.values()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.position))
                .map(CreatorParameter::value)
                .collect(Collectors.toList())
                .toArray(new Object[]{});
    }

    protected Parameters copyOf() {
        return new Parameters(this.creator, this.indexedParameters);
    }
}
