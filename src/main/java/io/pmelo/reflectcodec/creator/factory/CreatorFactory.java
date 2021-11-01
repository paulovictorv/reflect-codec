package io.pmelo.reflectcodec.creator.factory;

import io.pmelo.reflectcodec.creator.Creator;
import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.creator.Parameters;
import io.pmelo.reflectcodec.creator.exception.NoCreatorDefinedException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.pmelo.reflectcodec.creator.factory.parameters.CreatorParameterStrategy;
import io.pmelo.reflectcodec.creator.factory.parameters.CreatorParameterStrategyFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;

/**
 * Responsible for creating a Creator given a class.
 *
 * If the class is abstract, an effective "tree" of creators is derived. If a type can be directly instantiated, it'll
 * have a Concrete Creator assigned, otherwise an Abstract Creator is assigned. Such difference is transparent to the
 * caller. The Creator takes care of resolving the correct creator to use given a set of Parameters and a typeKeyId.
 *
 * It makes no sense to have a Creator to an abstract class, since you can't create an instance for it
 */
public class CreatorFactory {

    /**
     * Returns a Creator for a given type
     * @param type the type to create a Creator from
     * @return the created Creator
     */
    public static Creator create(Class<?> type) {
        Optional<Creator> creator;
        if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
            Map<String, Creator> subtypes = getCreatorsForEachSubtype(type);
            String typeKeyId = type.getAnnotation(JsonTypeInfo.class).property();
            creator = Optional.of(Creator.abstractCreator(type, subtypes, typeKeyId));
        } else {
            creator = Arrays.stream(type.getDeclaredConstructors())
                    .map(constructor -> {
                        CreatorParameterStrategy strategy = CreatorParameterStrategyFactory.resolve(constructor);
                        List<CreatorParameter> creatorParameters = strategy.extractCreatorParameters(constructor);
                        if (!creatorParameters.isEmpty()) {
                            Parameters parameters = new Parameters(type.getSimpleName(), creatorParameters);
                            return Creator.concreteCreator(type, constructor, parameters);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst();
        }
        return creator.orElseThrow(() -> new NoCreatorDefinedException(type));
    }

    private static Map<String, Creator> getCreatorsForEachSubtype(Class<?> type) {
        Map<String, Creator> subtypes = new HashMap<>();
        Arrays.stream(type.getAnnotation(JsonSubTypes.class).value())
                .forEach(jsonSubtype -> {
                    String name = jsonSubtype.name();
                    Class<?> subtypeType = jsonSubtype.value();
                    subtypes.put(name, create(subtypeType));
                });
        return subtypes;
    }
}
