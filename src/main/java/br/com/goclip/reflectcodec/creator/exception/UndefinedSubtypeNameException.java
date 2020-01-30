package br.com.goclip.reflectcodec.creator.exception;

public class UndefinedSubtypeNameException extends RuntimeException {
    public UndefinedSubtypeNameException(Class<?> type) {
        super(String.format("Not defined subtype name to some %s subclass", type.getSimpleName()));
    }
}
