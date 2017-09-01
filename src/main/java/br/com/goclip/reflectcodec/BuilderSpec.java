package br.com.goclip.reflectcodec;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 15/06/17.
 */
public class BuilderSpec {

    public final Class<?> targetClass;
    private final List<BuilderParameter> parameters;

    public BuilderSpec(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.parameters = new ArrayList<>();
    }

    public BuilderSpec addParameter(BuilderParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public ObjectBuilder builder() {
        try {
            Constructor<?> constructor = this.targetClass.getConstructor(parameters());
            return new ObjectBuilder(this.parameters, constructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public Class<?>[] parameters() {
        Class<?>[] classes = this.parameters.stream()
                .map(par -> par.type)
                .toArray(Class<?>[]::new);
        return classes;
    }
}
