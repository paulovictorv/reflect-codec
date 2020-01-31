package br.com.goclip.reflectcodec.creator.exception;

public class IncompatibleTypesException extends RuntimeException {
    public IncompatibleTypesException(Class<?> targetType, Class<?> givenType) {
        super(String.format("Given %s is not assignable to %s", givenType.getSimpleName(), targetType.getSimpleName()));
    }
}
