package br.com.goclip.reflectcodec;

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
                        //if parameter is a collection, lets decode it
                        //getting the actual generic type to decode it correctly
                        Collection dynamic = buildCollection(builderParameter.type);
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            Object decode = this.registry.get(builderParameter.genericType).decode(reader, decoderContext);
                            dynamic.add(decode);
                        }
                        reader.readEndArray();
                        return dynamic;
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

    /***
     * Creates an instance of the Collection being decoded.
     * If it is an abstract type, we need to define a default implementation otherwise
     * we assume it has an constructor that accepts a collection
     * @param type
     * @return
     */

    private Collection buildCollection(Class<?> type) {
        if (type.isInterface()) { //
            //TODO define this as an hotspot
            if (Set.class.isAssignableFrom(type)) {
                return new HashSet<>();
            } else if (Queue.class.isAssignableFrom(type)) {
                return new LinkedList<>();
            } else {
                return new ArrayList<>();
            }
        } else {
            try {
                return (Collection) type.getConstructor().newInstance();
            } catch (Exception e) {
                //if it doesn't, we scream
                throw new RuntimeException("Unsupported Collection: " + type.getSimpleName());
            }
        }
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
