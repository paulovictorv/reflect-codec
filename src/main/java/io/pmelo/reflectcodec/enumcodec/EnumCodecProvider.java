package io.pmelo.reflectcodec.enumcodec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Provides a codec for generic enum's
 */
public class EnumCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (Enum.class.isAssignableFrom(clazz)) {
            return (Codec<T>) new EnumCodec(clazz);
        }
        return null;
    }
}
