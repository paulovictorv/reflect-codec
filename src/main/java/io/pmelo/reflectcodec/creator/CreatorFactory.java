package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.exception.NoCreatorDefinedException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class CreatorFactory {

    public static Creator create(Class<?> type) {
        Optional<Creator> creator;
        if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
            Map<String, Creator> subtypes = getSubtypes(type);
            String property = type.getAnnotation(JsonTypeInfo.class).property();
            creator = Optional.ofNullable(Creator.create().withType(type).withSubtypes(subtypes).withTypeKeyId(property));
        } else {
            creator = Arrays.stream(type.getDeclaredConstructors())
                    .map(constructor -> {
                        List<CreatorParameter> creatorParameters = createCreatorParameters(constructor);
                        if (!creatorParameters.isEmpty()) {
                            Parameters parameters = new Parameters(type.getSimpleName(), creatorParameters);
                            return Creator.create().withConstructor(constructor).withType(type).withParameters(parameters);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst();
        }
        return creator.orElseThrow(() -> new NoCreatorDefinedException(type));
    }

    private static List<CreatorParameter> createCreatorParameters(Constructor<?> constructor) {
        JsonCreator jsonCreatorAnnotation = constructor.getAnnotation(JsonCreator.class);
        ConstructorProperties constructorPropertiesAnnotation = constructor.getAnnotation(ConstructorProperties.class);
        Parameter[] parameters1 = constructor.getParameters();
        if (jsonCreatorAnnotation != null) {
            return IntStream.range(0, parameters1.length).mapToObj(position -> {
                String parameterName;
                Parameter parameter = parameters1[position];
                parameterName = parameter.getAnnotation(JsonProperty.class).value();
                return CreatorParameter.create(parameter, position, parameterName);
            }).collect(toList());
        } else if (constructorPropertiesAnnotation != null) {
            String[] parameterNames = constructorPropertiesAnnotation.value();
            return IntStream.range(0, parameters1.length).mapToObj(position -> {
                String parameterName;
                Parameter parameter = parameters1[position];
                parameterName = parameterNames[position];
                return CreatorParameter.create(parameter, position, parameterName);
            }).collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static Map<String, Creator> getSubtypes(Class<?> type) {
        Map<String, Creator> subtypes = new HashMap<>();
        Arrays.stream(type.getAnnotation(JsonSubTypes.class)
                .value())
                .forEach(jsonSubtype -> {
                    String name = jsonSubtype.name();
                    Class<?> subtypeType = jsonSubtype.value();
                    for (Constructor<?> cons : subtypeType.getDeclaredConstructors()) {
                        List<CreatorParameter> creatorParameters = createCreatorParameters(cons);
                        if (!creatorParameters.isEmpty()) {
                            Creator creator = Creator.create()
                                    .withConstructor(cons)
                                    .withType(subtypeType)
                                    .withParameters(new Parameters(type.getSimpleName(), creatorParameters));
                            subtypes.put(name, creator);
                            break;
                        }
                    }
                });
        return subtypes;
    }
}
