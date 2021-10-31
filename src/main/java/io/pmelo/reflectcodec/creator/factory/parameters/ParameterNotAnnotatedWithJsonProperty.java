package io.pmelo.reflectcodec.creator.factory.parameters;

import java.lang.reflect.Constructor;

public class ParameterNotAnnotatedWithJsonProperty extends RuntimeException {
    public ParameterNotAnnotatedWithJsonProperty(int parameterPosition, Constructor<?> constructor) {
        super(String.format(
                "Parameter at position %d declared on class %s is not annotated with @JsonProperty.",
                parameterPosition,
                constructor.getDeclaringClass().getSimpleName()
        ));
    }
}
