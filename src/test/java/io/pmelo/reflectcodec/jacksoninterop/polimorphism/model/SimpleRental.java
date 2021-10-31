package io.pmelo.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleRental extends Rental {

    public final Person person;

    @JsonCreator
    public SimpleRental(@JsonProperty("_id") String id,
                        @JsonProperty("person") Person person) {
        super(id, Type.SIMPLE_RENTAL);
        this.person = person;
    }
}
