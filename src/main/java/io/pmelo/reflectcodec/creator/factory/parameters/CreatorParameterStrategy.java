package io.pmelo.reflectcodec.creator.factory.parameters;

import io.pmelo.reflectcodec.creator.CreatorParameter;

import java.lang.reflect.Constructor;
import java.util.List;

public interface CreatorParameterStrategy {

    List<CreatorParameter> extractCreatorParameters(Constructor<?> constructor);

}
