package io.pmelo.reflectcodec.creator.exception;

public class UnsupportedCollectionException extends RuntimeException {

    public UnsupportedCollectionException(Class<?> unsupportedCollection) {
        super(String.format("Unsupported collection: %s. Make sure that it has an empty constructor.",
                unsupportedCollection.getSimpleName()));
    }
}
