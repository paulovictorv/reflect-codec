package io.pmelo.reflectcodec.creator.exception;

public class UndefinedSubtypeNameException extends RuntimeException {
    public UndefinedSubtypeNameException(Class<?> type) {
        super(String.format("No defined subtype key ID for subclass %s. Refer to Jackson's documentation on polymorphic deserialization.", type.getSimpleName()));
    }
}
