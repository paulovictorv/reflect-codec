package br.com.goclip.reflectcodec.collections;

import br.com.goclip.reflectcodec.codec.util.Encoder;
import br.com.goclip.reflectcodec.creator.CreatorParameter;
import br.com.goclip.reflectcodec.creator.exception.UnsupportedCollectionException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.*;

public class CollectionCodec<T extends Collection> implements Codec<T> {

    private final CodecRegistry registry;
    private final CreatorParameter creatorParameter;
    private final Encoder encoder;

    public CollectionCodec(CodecRegistry registry, CreatorParameter creatorParameter) {
        this.registry = registry;
        this.creatorParameter = creatorParameter;
        this.encoder = new Encoder(registry);
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        //if parameter is a collection, lets decode it
        //getting the actual generic type to decode it correctly
        Collection dynamic = buildCollection(creatorParameter.type);
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            Object decode = this.registry.get(creatorParameter.genericType).decode(reader, decoderContext);
            dynamic.add(decode);
        }
        reader.readEndArray();
        return (T) dynamic;
    }

    @Override
    public void encode(BsonWriter writer, Collection value, EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }

    @Override
    public Class<T> getEncoderClass() {
        return (Class<T>) this.creatorParameter.type;
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
                throw new UnsupportedCollectionException("Unsupported Collection: " + type.getSimpleName());
            }
        }
    }
}
