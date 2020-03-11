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

    private String typeName;
    private Map<String, CreatorParameter> indexedParameters;

    public Parameters(String name, List<CreatorParameter> parameters) {
        this.typeName = name;
        this.indexedParameters = parameters.stream()
                .collect(Collectors.toMap(p -> p.name, Function.identity()));
    }

    public String getTypeKey(String parameterName) {
        return String.valueOf(this.indexedParameters.get(parameterName).value());
    }

    public void assignValue(String parameterName, Function<CreatorParameter, Object> computingFunction) {
            var result = indexedParameters.computeIfPresent(parameterName,
                    (key, creatorParameter) -> creatorParameter.withValue(computingFunction.apply(creatorParameter) ));
            if (result == null) {
                throw new AttributeNotMapped(this.typeName, parameterName);
            }
    }

    public Object[] sortedValues() {
        Object[] objects = indexedParameters.values()
                .stream()
                .sorted(Comparator.comparingInt(o -> o.position))
                .map(CreatorParameter::value)
                .collect(Collectors.toList())
                .toArray(new Object[]{});
        return objects;
    }

    protected Parameters copyOf() {
        HashMap<String, CreatorParameter> objectObjectHashMap = new HashMap<>();
        this.indexedParameters.entrySet().forEach(es -> {
            CreatorParameter cp = es.getValue();
            objectObjectHashMap.put(es.getKey(), cp.withValue(null));
        });
        return new Parameters(this.typeName, objectObjectHashMap);
    }
}
