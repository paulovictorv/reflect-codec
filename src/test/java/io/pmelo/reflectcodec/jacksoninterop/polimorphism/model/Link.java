package io.pmelo.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleRental.class, name = "SIMPLE_RENTAL"),
        @JsonSubTypes.Type(value = CategoryRental.class, name = "CATEGORY_RENTAL")
})
public abstract class Link {
    public enum Type { SIMPLE_RENTAL, CATEGORY_RENTAL }

    public final String _id;
    public final Type type;

    protected Link(String id,
                   Type type) {
        _id = id;
        this.type = type;
    }
}
