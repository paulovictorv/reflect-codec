package br.com.goclip.reflectcodec;

import java.util.Objects;

/**
 * This class represents the details of each constructor argument. The following info is stored for each attribute:
 * order: the declaration order of the argument. Needed for reflection invocation.
 * name: argument name as specified in its annotation
 * type: argument type, supports generic types
 * value: the value the corresponding argument should map to
 * Created by paulo on 15/06/17.
 */
public class BuilderParameter implements Comparable<BuilderParameter> {

    public final int order;
    public final String name;
    public final Class<?> type;
    public final Class<?> genericType;
    public final Object value;

    public BuilderParameter(int order, String name, Class<?> type) {
        this.order = order;
        this.name = name;
        this.type = type;
        this.genericType = null;
        this.value = null;
    }

    public BuilderParameter(int order, String name, Class<?> type, Class<?> genericType) {
        this.order = order;
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.value = null;
    }

    public BuilderParameter(int order, String name, Class<?> type, Class<?> genericType, Object value) {
        this.order = order;
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.value = value;
    }

    public BuilderParameter withValue(Object value) {
        return new BuilderParameter(this.order, this.name, this.type, this.genericType, value);
    }

    public Object value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "BuilderParameter{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuilderParameter that = (BuilderParameter) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(BuilderParameter o) {
        return Integer.compare(this.order, o.order);
    }

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }
}

