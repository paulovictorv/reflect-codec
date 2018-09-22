package br.com.goclip.reflectcodec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by paulo on 16/06/17.
 */
public class ObjectSpecCache {

    private final String packageName;
    private final Map<Class<?>, BuilderSpec> cache;
    private final HashMap<Class<?>, CompositeBuilderSpec> compositeCache;

    public ObjectSpecCache(String packageName) {
        this.packageName = packageName;
        cache = new HashMap<>();
        compositeCache = new HashMap<>();
    }

    boolean hasPackageName(Class<?> cachedClass) {
        Package aPackage = cachedClass.getPackage();
        return aPackage != null && aPackage.getName().contains(packageName);
    }

    public BuilderSpec get(Class<?> cachedClass) {
        BuilderSpec specification = this.cache.get(cachedClass);
        if (specification == null) {
            BuilderSpec spec = createSpec(cachedClass);
            this.cache.put(cachedClass, spec);
            return spec;
        } else {
            return specification;
        }
    }

    public CompositeBuilderSpec getComposite(Class<?> cachedClass) {
        CompositeBuilderSpec specification = this.compositeCache.get(cachedClass);
        if (specification == null) {
                CompositeBuilderSpec compositeSpec = createCompositeSpec(cachedClass);
                this.compositeCache.put(cachedClass, compositeSpec);
                return compositeSpec;
        } else {
            return specification;
        }
    }

    private BuilderSpec createSpec(TypeName typeName) {
        return createSpec(typeName.value).withName(typeName.name);
    }

    private CompositeBuilderSpec createCompositeSpec(Class<?> cachedClass) {
        String typePropertyName = cachedClass.getAnnotationsByType(JsonTypeInfo.class)[0].property();
        CompositeBuilderSpec compositeBuilderSpec = new CompositeBuilderSpec(typePropertyName);
        JsonSubTypes jsonSubTypes = cachedClass.getAnnotationsByType(JsonSubTypes.class)[0];
        Stream.of(jsonSubTypes.value())
                .map(type -> new TypeName(type.name(), type.value()))
                .map(this::createSpec)
                .peek(builderSpec -> {
                    this.cache.put(builderSpec.targetClass, builderSpec);
                }).forEach(compositeBuilderSpec::addBuilderSpec);
        return compositeBuilderSpec;
    }

    private BuilderSpec createSpec(Class<?> cachedClass) {
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
