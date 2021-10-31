package io.pmelo.reflectcodec.creator.factory.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pmelo.reflectcodec.creator.CreatorParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Takes a constructor, annotated with @JsonCreator and @JsonProperty and converts into a Creator
 */
class JsonCreatorCreatorParameterStrategy implements CreatorParameterStrategy {

    /**
     * Reads through the parameter list of a given constructor, introspecting the JsonProperty annotation of each to
     * derive its name.
     *
     * @param constructor the constructor to be introspected
     * @return a list of CreatorParameter
     * @throws ParameterNotAnnotatedWithJsonProperty if while reading the parameters, one of them is not annotated with
     * JsonProperty
     */
    public List<CreatorParameter> extractCreatorParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        return IntStream.range(0, parameters.length).mapToObj(position -> {
            String parameterName;
            Parameter parameter = parameters[position];
            JsonProperty annotation = parameter.getAnnotation(JsonProperty.class);

            if (annotation == null) {
                throw new ParameterNotAnnotatedWithJsonProperty(position, constructor);
            }

            parameterName = annotation.value();

            return CreatorParameter.create(parameter, position, parameterName);
        }).collect(toList());
    }

}
