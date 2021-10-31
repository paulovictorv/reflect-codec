package io.pmelo.reflectcodec;

import io.pmelo.reflectcodec.codec.util.Encoder;
import io.pmelo.reflectcodec.collections.CollectionCodec;
import io.pmelo.reflectcodec.creator.Creator;
import io.pmelo.reflectcodec.creator.Parameters;
import io.pmelo.reflectcodec.creator.exception.AttributeNotMapped;
import io.pmelo.reflectcodec.codec.util.PrimitiveUtils;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;

/**
 * Created by paulo on 10/06/17.
 *
 * Responsible for encoding/decoding arbitrary objects. It correctly dispatches already supported types to other codecs
 * and is able to recursively call itself to encode/decode other user defined types.
 *
 * This class is instantiated by the driver while setting up the codec provider chain
 */
public class DomainModelCodec implements Codec<Object> {
    private final CodecRegistry registry;
    private final Creator creator;
    private final Encoder encoder;

    public DomainModelCodec(CodecRegistry registry, Creator creator) {
        this.registry = registry;
        this.creator = creator;
        this.encoder = new Encoder(registry);
    }

    /**
     * Decodes a Mongodb BSON byte stream to an instance of the corresponding Java class.
     * @param reader a bson reader instance, provided by the driver
     * @param decoderContext container object for further configuration
     * @return an instance of the class configured under `Creator`
     */
    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        Parameters parameters = creator.parameters();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) { //reading through key, value pairs in the document
            String fieldName = reader.readName();
            try {
                //given a field name, pass a function that's responsible to decode the value into the parameter
                parameters.assignValue(fieldName, creatorParameter -> {
                    if (reader.getCurrentBsonType() == BsonType.NULL) {
                        //TODO config if we should always read/write null values
                        reader.readNull();
                        return null;
                    } else if (creatorParameter.type.isPrimitive()) {
                        //TODO remove special treatment for primitives, since it doesn't work anyway
                        return this.registry.get(PrimitiveUtils.mapToBoxedType(creatorParameter.type)).decode(reader, decoderContext);
                    } else if (Collection.class.isAssignableFrom(creatorParameter.type)
                            && reader.getCurrentBsonType() == BsonType.ARRAY) {
                        //special treatment for Collections, otherwise it gets decoded as a BsonArray
                        return new CollectionCodec(this.registry, creatorParameter).decode(reader, decoderContext);
                    } else {
                        //let the codec chain decode whatever gets read
                        Object decode = this.registry.get(creatorParameter.type).decode(reader, decoderContext);
                        return decode;
                    }
                });
            } catch (AttributeNotMapped e) {
                //TODO config if we should always ignore unmapped or throw exception
                reader.skipValue();
            }
        }
        reader.readEndDocument();
        return creator.newInstance(parameters);
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
