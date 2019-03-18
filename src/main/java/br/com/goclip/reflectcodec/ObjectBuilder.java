package br.com.goclip.reflectcodec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by paulo on 16/06/17.
 */
public class ObjectBuilder {

    private final Map<String, BuilderParameter> parameterMap;
    private final Constructor<?> constructor;

    public ObjectBuilder(List<BuilderParameter> parameters, Constructor<?> constructor) {
        this.parameterMap = parameters.stream()
                .collect(Collectors.toMap(BuilderParameter::name, Function.identity()));
        this.constructor = constructor;
    }

    public boolean hasField(String fieldName) {
        return parameterMap.containsKey(fieldName);
    }

    public void mapValue(String name, Function<BuilderParameter, Object> mapper) {
        parameterMap.computeIfPresent(name, (key, par) -> mapper
                .andThen(par::withValue)
                .apply(par));
    }

    /***
     * Generates an ordered array of arguments values to reflectively invoke a constructor
     * @return attribute values ordered list
     */

    private Object[] values() {
        return parameterMap.values().stream()
                .sorted()
                .map(BuilderParameter::value)
                .toArray(Object[]::new);
    }

    /***
     * Create an instance of target class using constructor field and its parameters
     * @return an instance of target class
     */

    public Object build() {
        try {
            return constructor.newInstance(values());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }
}