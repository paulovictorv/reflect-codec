package br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryRental extends Rental {
    @JsonCreator
    public CategoryRental(@JsonProperty("_id") String id) {
        super(id, Link.Type.CATEGORY_RENTAL);
    }
}
