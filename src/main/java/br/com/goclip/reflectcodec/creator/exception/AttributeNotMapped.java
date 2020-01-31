package br.com.goclip.reflectcodec.creator.exception;

public class AttributeNotMapped extends RuntimeException {
    public AttributeNotMapped(String className, String name) {
        super(String.format("Field %s not found in class %s.", name, className));
    }
}
