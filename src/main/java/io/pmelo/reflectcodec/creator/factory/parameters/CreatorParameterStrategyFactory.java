package io.pmelo.reflectcodec.creator.factory.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Factory that creates instances responsible for introspecting a constructor and deciding which way to extract
 * the argumentNames and their positions
 */
public class CreatorParameterStrategyFactory {

    /**
     * FactoryMethod resolving which strategy to use to read constructor data
     * @param constructor the desired constructor to introspect
     * @return the correct strategy
     */
    public static CreatorParameterStrategy resolve(Constructor<?> constructor) {
        Annotation constructorAnnotation = constructor.getAnnotation(JsonCreator.class) != null ?
                constructor.getAnnotation(JsonCreator.class)
                : constructor.getAnnotation(ConstructorProperties.class);

       return resolve(constructorAnnotation);
    }

    /**
     * FactoryMethod resolving which strategy to use to read constructor data
     * @param constructorAnnotation the annotation used on the constructor
     * @return the correct strategy
     */
    public static CreatorParameterStrategy resolve(Annotation constructorAnnotation) {
        if (constructorAnnotation instanceof JsonCreator) {
            return new JsonCreatorCreatorParameterStrategy();
        } else if (constructorAnnotation instanceof ConstructorProperties) {
            return new ConstructorPropertiesCreatorParametersStrategy((ConstructorProperties) constructorAnnotation);
        } else {
            return constructor -> List.of();
        }
    }

}
