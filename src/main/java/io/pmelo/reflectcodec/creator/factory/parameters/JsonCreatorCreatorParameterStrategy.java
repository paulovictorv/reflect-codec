package io.pmelo.reflectcodec.creator.factory.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.creator.factory.parameters.CreatorParameterStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

class JsonCreatorCreatorParameterStrategy implements CreatorParameterStrategy {
    public List<CreatorParameter> extractCreatorParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        return IntStream.range(0, parameters.length).mapToObj(position -> {
            String parameterName;
            Parameter parameter = parameters[position];
            parameterName = parameter.getAnnotation(JsonProperty.class).value();
            return CreatorParameter.create(parameter, position, parameterName);
        }).collect(toList());
    }
}
