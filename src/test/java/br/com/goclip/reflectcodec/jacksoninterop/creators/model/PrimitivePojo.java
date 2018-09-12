package br.com.goclip.reflectcodec.jacksoninterop.creators.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrimitivePojo {

    public final int integer;
    public final char character;
    public final long aLong;
    public final double aDouble;
    public final float aFloat;
    public final byte aByte;

    @JsonCreator
    public PrimitivePojo(@JsonProperty("integer") int integer,
                         @JsonProperty("character") char character,
                         @JsonProperty("aLong") long aLong,
                         @JsonProperty("aDouble") double aDouble,
                         @JsonProperty("aFloat") float aFloat,
                         @JsonProperty("aByte") byte aByte) {
        this.integer = integer;
        this.character = character;
        this.aLong = aLong;
        this.aDouble = aDouble;
        this.aFloat = aFloat;
        this.aByte = aByte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitivePojo that = (PrimitivePojo) o;

        if (integer != that.integer) return false;
        if (character != that.character) return false;
        if (aLong != that.aLong) return false;
        if (Double.compare(that.aDouble, aDouble) != 0) return false;
        if (Float.compare(that.aFloat, aFloat) != 0) return false;
        return aByte == that.aByte;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = integer;
        result = 31 * result + (int) character;
        result = 31 * result + (int) (aLong ^ (aLong >>> 32));
        temp = Double.doubleToLongBits(aDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (aFloat != +0.0f ? Float.floatToIntBits(aFloat) : 0);
        result = 31 * result + (int) aByte;
        return result;
    }
}
