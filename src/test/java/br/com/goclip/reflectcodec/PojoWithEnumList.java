package br.com.goclip.reflectcodec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class PojoWithEnumList {

    public enum TestEnum {VALUE_1, VALUE_2}

    public final String name;
    public final List<TestEnum> enums;

    @JsonCreator
    public PojoWithEnumList(@JsonProperty("name") String name,
                            @JsonProperty("enums") List<TestEnum> enums) {
        this.name = name;
        this.enums = enums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PojoWithEnumList that = (PojoWithEnumList) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(enums, that.enums);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, enums);
    }
}
