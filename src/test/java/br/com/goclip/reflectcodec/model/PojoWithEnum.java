package br.com.goclip.reflectcodec.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PojoWithEnum {

    public enum TestEnum {VALUE_1, VALUE_2}

    public final String name;
    public final TestEnum testEnum;

    @JsonCreator
    public PojoWithEnum(@JsonProperty("name") String name,
                        @JsonProperty("testEnum") TestEnum testEnum) {
        this.name = name;
        this.testEnum = testEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PojoWithEnum that = (PojoWithEnum) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return testEnum == that.testEnum;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (testEnum != null ? testEnum.hashCode() : 0);
        return result;
    }
}
