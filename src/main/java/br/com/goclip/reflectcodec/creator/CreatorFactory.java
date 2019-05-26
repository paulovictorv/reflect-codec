package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.NoCreatorDefinedException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CreatorFactory {

    public static Creator createSingle(Class<?> type) {
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();

        Optional<Creator> classCreator = Arrays.stream(declaredConstructors)
                .filter(constructor -> constructor.getDeclaredAnnotation(JsonCreator.class) != null)
                .map(constructor -> Creator.create()
                        .withType(type)
                        .withConstructor(constructor))
                .map(creator -> {
                    Parameter[] parameters = creator.constructor.getParameters();
                    List<CreatorParameter> collect = IntStream.range(0, parameters.length)
                            .mapToObj(position -> {
                                Parameter parameter = parameters[position];
                                JsonProperty annotation = parameter.getAnnotation(JsonProperty.class);

                                return extractGenericType(parameter)
                                        .map(genericType -> CreatorParameter.createGeneric(position,
                                                parameter.getType(),
                                                genericType,
                                                annotation.value()))
                                        .orElse(CreatorParameter.create(position, parameter.getType(), annotation.value()));
                            })
                            .collect(toList());

                    return creator.withParameters(collect);
                })
                .findFirst()
                .or(() -> Arrays.stream(declaredConstructors)
                        .filter(constructor -> constructor.getDeclaredAnnotation(ConstructorProperties.class) != null)
                        .map(constructor -> Creator.create()
                                .withType(type)
                                .withConstructor(constructor))
                        .map(creator -> {
                            String[] parameterNames = creator.constructor.getDeclaredAnnotation(ConstructorProperties.class).value();
                            Parameter[] parameters = creator.constructor.getParameters();
                            List<CreatorParameter> collect = IntStream.range(0, parameters.length)
                                    .mapToObj(position -> {
                                        String parameterName = parameterNames[position];
                                        Parameter parameter = parameters[position];

                                        return extractGenericType(parameter)
                                                .map(genericType -> CreatorParameter.createGeneric(position,
                                                        parameter.getType(),
                                                        genericType,
                                                        parameterName)
                                                ).orElse(CreatorParameter.create(position,
                                                        parameter.getType(), parameterName));
                                    })
                                    .collect(toList());

                            return creator.withParameters(collect);
                        }).findFirst());

        return classCreator.orElseThrow(() -> new NoCreatorDefinedException(type));
    }

    private static Optional<Class<?>> extractGenericType(Parameter parameter) {
        Type parameterizedType = parameter.getParameterizedType();
        if (parameterizedType instanceof ParameterizedType) {
            Type type = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
            return Optional.ofNullable((Class<?>) type);
        } else {
            return Optional.empty();
        }
    }
}
