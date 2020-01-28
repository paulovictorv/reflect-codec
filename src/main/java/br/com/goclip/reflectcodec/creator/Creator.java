package br.com.goclip.reflectcodec.creator;

import lombok.Data;
import lombok.With;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Data
@With
public class Creator {

    public static Creator create() {
        return new Creator(null, null,  null);
    }

    public final Class<?> type;
    public final Class<?> concreteType;
    public final String typeId;
    private final Constructor<?> constructor;
    public final Map<String, Creator> instanceAttributes;

    public Creator(Class<?> type,
                   Class<?> concreteType,
                   Map<Constructor<?>, Parameters> instanceAttributes) {
        this.type = type;
        this.concreteType = concreteType;
        this.instanceAttributes = instanceAttributes;
    }

    public Object newInstance(Parameters parameters) {
        if (this.constructor == null) { //I'm abstract
            String typeName = parameters.getTypeAttribute();
            return instanceAttributes.get(typeName).newInstance(parameters.sortedValues());
        } else {
            return constructor.newInstance(parameters.sortedValues());
        }
    }

    public Object newInstance(Map<Constructor<?>, Parameters> instanceAttributes) {
        try {
            Constructor<?> constructor = instanceAttributes.keySet().stream().findFirst().get();
            Parameters parameters = instanceAttributes.get(constructor);
            return constructor.newInstance(parameters.sortedValues());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
