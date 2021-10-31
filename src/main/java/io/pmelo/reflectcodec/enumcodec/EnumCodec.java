package io.pmelo.reflectcodec.enumcodec;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Encodes and decodes to and from enums, using the name as key
 *
 * @param <T> any enum
 */
public class EnumCodec<T extends Enum<T>> implements Codec<T> {
    private final Class<T> clazz;

    EnumCodec(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void encode(final BsonWriter writer, final T value, final EncoderContext encoderContext) {
        writer.writeString(value.name());
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }

    @Override
    public T decode(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == BsonType.NULL) {
            reader.readNull();
            return null;
        } else {
            return Enum.valueOf(clazz, reader.readString());
        }
    }
}
