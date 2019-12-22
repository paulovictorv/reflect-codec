package br.com.goclip.reflectcodec;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/***
 * This class contains all the information necessary to create a concrete instance for a abstract type.
 * It contains the following info:
 * typeProperty: contain information as type, order, name, value etc of target class
 * classToBuilder: {@link @BulderSpec}
 *
 */
public class CompositeBuilderSpec {
    private final BuilderParameter typeProperty;
    private final Map<Class<?>, BuilderSpec> classToBuilder;
    private final Map<String, BuilderSpec> typeNameToBuilder;

    public CompositeBuilderSpec(String typeProperty) {
        this.typeProperty = new BuilderParameter(0, typeProperty, String.class);
        this.classToBuilder = new HashMap<>();
        this.typeNameToBuilder = new HashMap<>();
    }

    public void addBuilderSpec(BuilderSpec builderSpec) {
        this.classToBuilder.put(builderSpec.targetClass, builderSpec);
        this.typeNameToBuilder.put(builderSpec.name, builderSpec);
    }

    public ObjectBuilder builder(List<PolymorphicDomainModelCodec.BsonToken> tokens) {
        ObjectBuilder resultingObjBuilder = tokens.stream()
                .filter(token -> token.name.equals(typeProperty.name))
                .findFirst()
                .map(token -> this.typeNameToBuilder.get(token.value))
                .map(BuilderSpec::builder)
                .orElseThrow(() -> new RuntimeException("Mapping not defined"));

        for (PolymorphicDomainModelCodec.BsonToken token : tokens) {
            resultingObjBuilder.mapValue(token.name, (param) -> token.value);
        }

        return resultingObjBuilder;
    }

    public Optional<Class<?>> typeFor(String parameterName) {
        if (typeProperty.name().equals(parameterName)) {
            return Optional.of(typeProperty.type());
        } else {
            //extract value from map
            Class<?> aClass = this.classToBuilder.values().stream()
                    //extract builder parameters
                    .map(BuilderSpec::builderParameters)
                    //map list of lists to a list
                    .flatMap(Collection::stream)
                    //deduplicating key names - will overwrite descendants key definitions
                    .collect(toSet()).stream()
                    //build a lookup map name -> type
                    .collect(toMap(BuilderParameter::name, BuilderParameter::type))
                    //get it
                    .get(parameterName);
            //we may try to look for a parameter that is not defined anywhere in our tree
            return Optional.ofNullable(aClass);
        }
    }
}
