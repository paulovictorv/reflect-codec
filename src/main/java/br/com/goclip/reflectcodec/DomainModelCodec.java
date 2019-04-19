package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.collections.CollectionCodec;
import br.com.goclip.reflectcodec.util.Encoder;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;

import static br.com.goclip.reflectcodec.util.PrimitiveUtils.mapToBoxedType;

/**
 * Created by paulo on 10/06/17.
 */
public class DomainModelCodec implements Codec<Object> {
    private final CodecRegistry registry;
    private final BuilderSpec builderSpec;
    private final Encoder encoder;

    public DomainModelCodec(CodecRegistry registry, BuilderSpec builderSpec) {
        this.registry = registry;
        this.builderSpec = builderSpec;
        this.encoder = new Encoder(registry);
    }

    /**
     * Decodes a Mongodb BSON byte stream to an instance of the corresponding Java class.
     * @param reader
     * @param decoderContext
     * @return target java object
     */
    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
        ObjectBuilder builder = builderSpec.builder();

        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();

            if (builder.hasField(fieldName)) {
                builder.mapValue(fieldName, builderParameter -> {
                    if (reader.getCurrentBsonType() == BsonType.NULL) {
                        reader.readNull();
                        return null;
                    } else if (builderParameter.type.isPrimitive()) {
                        return this.registry.get(mapToBoxedType(builderParameter.type)).decode(reader, decoderContext);
                    } else if (Collection.class.isAssignableFrom(builderParameter.type)
                            && reader.getCurrentBsonType() == BsonType.ARRAY) {
                        return new CollectionCodec(this.registry, builderParameter).decode(reader, decoderContext);
                    } else {
                        return this.registry.get(builderParameter.type).decode(reader, decoderContext);
                    }
                });
            } else {
                reader.skipValue();
            }

        }
        reader.readEndDocument();

        return builder.build();
    }

    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }

    @Override
    public Class<Object> getEncoderClass() {
        return null;
    }
}
