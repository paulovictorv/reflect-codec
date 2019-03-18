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

    /***
     * Verify if cachedClass belongs to packageName defined in the constructor
     * @param cachedClass
     * @return  {@code true} if {@code cachedClass} belongs to package otherwise {@code false}
     */
    boolean hasPackageName(Class<?> cachedClass) {
        Package aPackage = cachedClass.getPackage();
        return aPackage != null && aPackage.getName().contains(packageName);
    }


    /***
     * Tries to return a BuilderSpec of a Class.
     * If the BuilderSpec couldn't be found, a new one is generated, cached and returned
     * @param cachedClass
     * @return BuilderSpec of cachedClass
     */
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

    /***
     * Return {@Link CompositeBuilderSpec} contained in the compositeCache map, if it is null
     * create a new instance of cachedClass and put in compositeCache to be used later
     * @param cachedClass
     * @return CompositeBuilderSpec
     */

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

    /***
     * Create a compositeBuilderSpec of concrete subclass according as information
     * contain in the @JsonTypeInfo and @JsonSubTypes annotations
     * @param cachedClass
     * @return
     */
    private CompositeBuilderSpec createCompositeSpec(Class<?> cachedClass) {
        String typePropertyName = cachedClass.getAnnotationsByType(JsonTypeInfo.class)[0].property();
        CompositeBuilderSpec compositeBuilderSpec = new CompositeBuilderSpec(typePropertyName);
        JsonSubTypes jsonSubTypes = cachedClass.getAnnotationsByType(JsonSubTypes.class)[0];
        Stream.of(jsonSubTypes.value())
                .map(type -> new TypeName(type.name(), type.value()))
                .map(this::createSpec)
                .peek(builderSpec -> this.cache.put(builderSpec.targetClass, builderSpec))
                .forEach(compositeBuilderSpec::addBuilderSpec);
        return compositeBuilderSpec;
    }

    /***
     * Create a instance of BuilderSpec from cachedClass,
     * getting all parameters of constructor noted with @JsonCreator including his name and type
     * to create the BuilderParameters
     * The parameters names are the values contained in the @JsonProperty annotation
     * @param cachedClass represent class to be encoded/decode
     * @return a instance of BuilderSpec
     */

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