package br.com.goclip.reflectcodec.creator.exception;

public class UndefinedSubtypeNameException extends RuntimeException {
    public UndefinedSubtypeNameException(Class<?> type) {
        super(String.format("No defined subtype key ID for subclass %s", type.getSimpleName()));
    }
}
