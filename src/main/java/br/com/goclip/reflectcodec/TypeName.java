package br.com.goclip.reflectcodec;

public class TypeName {
    public final String name;
    public final Class<?> value;

    public TypeName(String name, Class<?> value) {
        this.name = name;
        this.value = value;
    }
}
