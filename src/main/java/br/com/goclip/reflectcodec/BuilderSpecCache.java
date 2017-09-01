package br.com.goclip.reflectcodec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paulo on 16/06/17.
 */
public class BuilderSpecCache {

    private final String packageName;
    private final Map<Class<?>, BuilderSpec> cache;

    public BuilderSpecCache(String packageName) {
        this.packageName = packageName;
        cache = new HashMap<>();
    }

    public boolean hasPackageName(Class<?> cachedClass) {
        Package aPackage = cachedClass.getPackage();
        return aPackage != null && aPackage.getName().equals(packageName);
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
                    builderSpec.addParameter(new BuilderParameter(order++, parameterName.value(), parameter.getType()));
                }
            }
        }

        return builderSpec;
    }
}
