package io.pmelo.reflectcodec.collections;

import io.pmelo.reflectcodec.codec.util.Encoder;
import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.creator.exception.UnsupportedCollectionException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;

/**
 * Decodes a BsonArray into one of Java's collection types, respecting the parametrized types and collection type
 * declared in the class
 */
public class CollectionCodec implements Codec<Collection> {

    private final CodecRegistry registry;
    private final CreatorParameter creatorParameter;
    private final Encoder encoder;

    public CollectionCodec(CodecRegistry registry, CreatorParameter creatorParameter) {
        this.registry = registry;
        this.creatorParameter = creatorParameter;
        this.encoder = new Encoder(registry);
    }

    @Override
    public Collection decode(BsonReader reader, DecoderContext decoderContext) {
        //if parameter is a collection, lets decode it
        //getting the actual generic type to decode it correctly
        Collection dynamic = buildCollection(creatorParameter.type);
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            Object decode = this.registry.get(creatorParameter.genericType).decode(reader, decoderContext);
            dynamic.add(decode);
        }
        reader.readEndArray();
        return dynamic;
    }

    @Override
    public void encode(BsonWriter writer, Collection value, EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }

    @Override
    public Class<Collection> getEncoderClass() {
        return (Class<Collection>) this.creatorParameter.type;
    }

    /***
     * Creates an instance of the Collection being decoded.
     * If it is an abstract type, we need to define a default implementation, otherwise
     * we assume it has an empty constructor
     *
     * @param type the type of the declared class field
     * @return an instance of the Collection
     */
    private Collection buildCollection(Class<?> type) {
        if (type.isInterface()) { //is it an interface?
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
                //if it doesn't have an empty constructor, raise exception
                throw new UnsupportedCollectionException(type);
            }
        }
    }
}
