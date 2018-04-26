package br.com.goclip.reflectcodec.model.inheritance;

import br.com.goclip.reflectcodec.Inheritance;
import br.com.goclip.reflectcodec.InheritanceMap;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Inheritance(mappedBy = "type")
@InheritanceMap(keyValue = "TYPE_A", impl = ConcreteClassA.class)
@InheritanceMap(keyValue = "TYPE_B", impl = ConcreteClassB.class)
public abstract class AbstractBaseClass {

    public final String id;

    @Discriminator
    public final TypeDiscriminator type;

    @JsonCreator
    protected AbstractBaseClass(@JsonProperty() String id, TypeDiscriminator type) {
        this.id = id;
        this.type = type;
    }
}

