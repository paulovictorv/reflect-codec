package io.pmelo.reflectcodec.creator.factory;

import io.pmelo.reflectcodec.creator.Creator;
import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.creator.Parameters;
import io.pmelo.reflectcodec.creator.exception.NoCreatorDefinedException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.pmelo.reflectcodec.creator.factory.parameters.CreatorParameterStrategy;
import io.pmelo.reflectcodec.creator.factory.parameters.CreatorParameterStrategyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;

public class CreatorFactory {

    public static Creator create(Class<?> type) {
        Optional<Creator> creator;
        if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
            Map<String, Creator> subtypes = getSubtypes(type);
            String property = type.getAnnotation(JsonTypeInfo.class).property();
            creator = Optional.of(Creator.create().withType(type).withSubtypes(subtypes).withTypeKeyId(property));
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
        CreatorParameterStrategy strategy = CreatorParameterStrategyFactory.resolve(constructor);
        return strategy.extractCreatorParameters(constructor);
    }

    private static Map<String, Creator> getSubtypes(Class<?> type) {
        Map<String, Creator> subtypes = new HashMap<>();
        Arrays.stream(type.getAnnotation(JsonSubTypes.class).value())
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
