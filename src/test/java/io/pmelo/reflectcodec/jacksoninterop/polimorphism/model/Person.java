package io.pmelo.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;

public class Person {

    public final String _id;
    public final String name;

    @JsonCreator
    public Person(@JsonProperty("_id") String id,
                  @JsonProperty("name") String name) {
        this._id = id;
        this.name = name;

    }
}
