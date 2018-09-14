package br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

    public final String name;

    @JsonCreator
    public Person(@JsonProperty("name") String name) {
        this.name = name;
    }
}
