package br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Person {

    public final String name;
    public final LocalDate birthday;

    @JsonCreator
    public Person(@JsonProperty("name") String name,
                  @JsonProperty("birthday") LocalDate birthday) {
        this.name = name;
        this.birthday = birthday;
    }
}
