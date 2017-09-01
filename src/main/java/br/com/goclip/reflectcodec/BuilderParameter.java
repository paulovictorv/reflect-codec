package br.com.goclip.reflectcodec;

/**
 * Created by paulo on 15/06/17.
 */
public class BuilderParameter implements Comparable<BuilderParameter> {

    public final int order;
    public final String name;
    public final Class<?> type;
    public final Object value;

    public BuilderParameter(int order, String name, Class<?> type) {
        this.order = order;
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public BuilderParameter(int order, String name, Class<?> type, Object value) {
        this.order = order;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public BuilderParameter withValue(Object value) {
        return new BuilderParameter(this.order, this.name, this.type, value);
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
    public int compareTo(BuilderParameter o) {
        return Integer.compare(this.order, o.order);
    }

    public String name() {
        return name;
    }
}

