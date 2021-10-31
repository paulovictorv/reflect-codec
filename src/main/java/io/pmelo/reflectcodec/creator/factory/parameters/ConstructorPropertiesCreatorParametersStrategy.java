package io.pmelo.reflectcodec.creator.factory.parameters;

import io.pmelo.reflectcodec.creator.CreatorParameter;
import lombok.AllArgsConstructor;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
class ConstructorPropertiesCreatorParametersStrategy implements CreatorParameterStrategy {

    private final ConstructorProperties constructorPropertiesAnnotation;

    public List<CreatorParameter> extractCreatorParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] parameterNames = constructorPropertiesAnnotation.value();
        return IntStream.range(0, parameters.length).mapToObj(position -> {
            String parameterName;
            Parameter parameter = parameters[position];
            parameterName = parameterNames[position];
            return CreatorParameter.create(parameter, position, parameterName);
        }).collect(toList());
    }

}
