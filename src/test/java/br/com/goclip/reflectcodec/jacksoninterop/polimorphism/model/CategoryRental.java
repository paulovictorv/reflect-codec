package br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryRental extends Link {

    public final Type type;

    @JsonCreator
    public CategoryRental(@JsonProperty("_id") String id,
                          @JsonProperty("type") Type type) {
        super(id, Link.Type.CATEGORY_RENTAL);
        this.type = type;
    }
}
