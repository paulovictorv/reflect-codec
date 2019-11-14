package br.com.goclip.reflectcodec.creator;

import lombok.Data;
import lombok.With;

import java.lang.reflect.Constructor;

@Data
@With
public class Creator {

    public static Creator create() {
        return new Creator(null, null, null, null);
    }

    public final Class<?> type;
    public final Class<?> concreteType;
    public final Constructor<?> constructor;
    private final Parameters parameters;

    public Creator(Class<?> type, Class<?> concreteType, Constructor<?> constructor, Parameters parameters) {
        this.type = type;
        this.concreteType = concreteType;
        this.constructor = constructor;
        this.parameters = parameters.withCreator(this);
    }

    public Parameters parameters() {
        return parameters.copyOf();
    }

    public Object newInstance() {
        try {
            return constructor.newInstance(parameters.sortedValues());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
