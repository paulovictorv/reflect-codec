package br.com.goclip.reflectcodec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PojoWithTransient {

    public transient final String ignored;

    @JsonCreator
    public PojoWithTransient(@JsonProperty("ignored") String ignored) {
        this.ignored = ignored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PojoWithTransient that = (PojoWithTransient) o;
        return Objects.equals(ignored, that.ignored);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ignored);
    }
}
