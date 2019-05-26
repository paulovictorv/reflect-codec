package br.com.goclip.reflectcodec.creator.exception;

public class NoCreatorDefinedException extends RuntimeException {
    public NoCreatorDefinedException(Class<?> type) {
        super(String.format("%s must have at least one constructor defined as creator", type.getSimpleName()));
    }
}
