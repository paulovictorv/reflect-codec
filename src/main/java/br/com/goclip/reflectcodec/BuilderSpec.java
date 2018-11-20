package br.com.goclip.reflectcodec;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 15/06/17.
 * This class represent a class to be encoded/decoded contained the follows information:
 * name : represent class name
 * targetClass: represent the class to be encoded/decoded
 * parameters: represent all parameters of constructor noted with @JsonCreator
 */
public class BuilderSpec {

    public final String name;
    public final Class<?> targetClass;
    private final List<BuilderParameter> parameters;

    public BuilderSpec(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.parameters = new ArrayList<>();
        name = null;
    }

    private BuilderSpec(Class<?> targetClass, List<BuilderParameter> parameters, String name) {
        this.targetClass = targetClass;
        this.parameters = parameters;
        this.name = name;
    }

    public BuilderSpec addParameter(BuilderParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public List<BuilderParameter> builderParameters() {
        return this.parameters;
    }

    public ObjectBuilder builder() {
        try {
            Constructor<?> constructor = this.targetClass.getConstructor(parameters());
            return new ObjectBuilder(this.parameters, constructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private Class<?>[] parameters() {
        Class<?>[] classes = this.parameters.stream()
                .map(par -> par.type)
                .toArray(Class<?>[]::new);
        return classes;
    }

    public BuilderSpec withName(String name) {
        return new BuilderSpec(targetClass, parameters, name);
    }
}
