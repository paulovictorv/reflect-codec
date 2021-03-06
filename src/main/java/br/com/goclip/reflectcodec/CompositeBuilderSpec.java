package br.com.goclip.reflectcodec;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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

    public ObjectBuilder builder(Class<?> type) {
        return classToBuilder.get(type).builder();
    }

    public ObjectBuilder builder(String typeName) {
        return typeNameToBuilder.get(typeName).builder();
    }

    public Optional<Class<?>> typeFor(String parameterName) {
        if (typeProperty.name().equals(parameterName)) {
            return Optional.of(typeProperty.type());
        } else {
            Class<?> aClass = this.classToBuilder.entrySet().stream()
                    //extract value from map
                    .map(Map.Entry::getValue)
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
