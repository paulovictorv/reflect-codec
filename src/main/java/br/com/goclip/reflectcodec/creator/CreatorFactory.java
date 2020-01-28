package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.NoCreatorDefinedException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CreatorFactory {

    public static Creator create(Class<?> type) {
        Optional<Creator> creator;
        if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
            Map<Constructor<?>, Parameters> subtypes = getSubtypeInstanceAttributes(type);
            creator = Optional.ofNullable(Creator.create().withType(type).withInstanceAttributes(subtypes));
        } else {
            creator = Arrays.stream(type.getDeclaredConstructors())
                    .filter(CreatorFactory::isMainConstructor)
                    .map(constructor -> Creator.create().withType(type)
                            .withInstanceAttributes(Map.of(constructor, new Parameters(type.getSimpleName(), createCreatorParameters(constructor)))))
                    .findFirst();
        }
        return creator.orElseThrow(() -> new NoCreatorDefinedException(type));
    }

    private static Map<Constructor<?>, Parameters> getSubtypeInstanceAttributes(Class<?> supertype) {
        List<Constructor<?>> constructors = extractSubclassConstructors(supertype);
        Map<Constructor<?>, Parameters> constructorAndParameters = new HashMap<>();
        constructors.forEach(constructor -> {
            Parameters parameters = new Parameters(supertype.getSimpleName(), createCreatorParameters(constructor));
            constructorAndParameters.put(constructor, parameters);
        });
        return constructorAndParameters;
    }

    private static List<CreatorParameter> createCreatorParameters(Constructor<?> constructor) {
        AtomicReference<AtomicReferenceArray<String>> parameterNames = new AtomicReference<>(new AtomicReferenceArray<>(new String[0]));
        Optional.ofNullable(constructor.getDeclaredAnnotation(ConstructorProperties.class))
                .ifPresent(constructorProperties -> parameterNames.set(new AtomicReferenceArray<>(constructorProperties.value())));
        Parameter[] parameters1 = constructor.getParameters();
        return IntStream.range(0, parameters1.length).mapToObj(position -> {
            String parameterName;
            Parameter parameter = parameters1[position];
            if (parameterNames.get().length() > 0) {
                parameterName = parameterNames.get().get(position);
            } else {
                parameterName = parameter.getAnnotation(JsonProperty.class).value();
            }
            return CreatorParameter.create(parameter, position, parameterName);
        }).collect(toList());
    }

    private static List<Constructor<?>> extractSubclassConstructors(Class<?> type) {
        return Arrays.stream(type.getAnnotation(JsonSubTypes.class)
                .value())
                .map(jacksonAnnotation -> {
                    Creator.create().w
                })
                .map(Class::getConstructors)
                .flatMap(Arrays::stream)
                .filter(CreatorFactory::isMainConstructor)
                .collect(toList());
    }

    private static boolean isMainConstructor(Constructor<?> constructor) {
        return constructor.getAnnotation(JsonCreator.class) != null
                || constructor.getAnnotation(ConstructorProperties.class) != null;
    }
}
