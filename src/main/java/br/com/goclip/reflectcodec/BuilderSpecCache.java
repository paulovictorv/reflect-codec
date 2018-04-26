package br.com.goclip.reflectcodec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paulo on 16/06/17.
 */
public class BuilderSpecCache {

    private final String packageName;
    private final Map<Class<?>, BuilderSpec> cache;
    private Map<Object, Class<?>> concreteResolutionMap;

    public BuilderSpecCache(String packageName) {
        this.packageName = packageName;
        cache = new HashMap<>();
    }

    public boolean hasPackageName(Class<?> cachedClass) {
        Package aPackage = cachedClass.getPackage();
        return aPackage != null && aPackage.getName().contains(packageName);
    }

    public BuilderSpec get(Class<?> cachedClass) {
        BuilderSpec specification = this.cache.get(cachedClass);
        if (specification == null) {
            if (cachedClass.isInterface()) {
                if (cachedClass.isAnnotationPresent(Inheritance.class)) {
                    Inheritance annotation = cachedClass.getAnnotation(Inheritance.class);
                    for (InheritanceMap inheritanceMap : cachedClass.getAnnotationsByType(InheritanceMap.class)) {
                        concreteResolutionMap.put(inheritanceMap.keyValue(), inheritanceMap.impl());
                        this.cache.put(inheritanceMap.impl(), createSpec(cachedClass));
                    }
                }
                return this.cache.get(cachedClass);
            } else {
                BuilderSpec spec = createSpec(cachedClass);
                this.cache.put(cachedClass, spec);
                return spec;
            }
        } else {
            return specification;
        }
    }

    public BuilderSpec createSpec(Class<?> cachedClass) {
        Constructor<?>[] constructors = cachedClass.getConstructors();

        BuilderSpec builderSpec = new BuilderSpec(cachedClass);

        for (Constructor<?> constructor : constructors) {
            JsonCreator annotationsByType = constructor.getAnnotation(JsonCreator.class);
            if (annotationsByType != null) {
                Parameter[] parameters = constructor.getParameters();
                int order = 0;
                for (Parameter parameter : parameters) {
                    JsonProperty parameterName = parameter.getAnnotation(JsonProperty.class);
                    Type parameterizedType = parameter.getParameterizedType();
                    if (parameterizedType instanceof ParameterizedType) {
                        Type type = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
                        builderSpec.addParameter(new BuilderParameter(order++, parameterName.value(), parameter.getType(), (Class<?>) type));
                    } else {
                        builderSpec.addParameter(new BuilderParameter(order++, parameterName.value(), parameter.getType()));
                    }
                }
            }
        }

        return builderSpec;
    }
}
