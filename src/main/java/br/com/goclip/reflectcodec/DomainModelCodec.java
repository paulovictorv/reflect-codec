package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.codec.util.Encoder;
import br.com.goclip.reflectcodec.collections.CollectionCodec;
import br.com.goclip.reflectcodec.creator.Creator;
import br.com.goclip.reflectcodec.creator.CreatorParameter;
import br.com.goclip.reflectcodec.creator.Parameters;
import br.com.goclip.reflectcodec.creator.exception.AttributeNotMapped;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.goclip.reflectcodec.codec.util.PrimitiveUtils.mapToBoxedType;

/**
 * Created by paulo on 10/06/17.
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
     * @param reader
     * @param decoderContext
     * @return target java object
     */
    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            try {
                creator.instanceAttributes.forEach((constructor, parameters1) -> parameters1.assignValue(fieldName, creatorParameter -> {
                    if (reader.getCurrentBsonType() == BsonType.NULL) {
                        //TODO config if we should always read/write null values
                        reader.readNull();
                        return null;
                    } else if (creatorParameter.type.isPrimitive()) {
                        return this.registry.get(mapToBoxedType(creatorParameter.type)).decode(reader, decoderContext);
                    } else if (Collection.class.isAssignableFrom(creatorParameter.type)
                            && reader.getCurrentBsonType() == BsonType.ARRAY) {
                        return new CollectionCodec(this.registry, creatorParameter).decode(reader, decoderContext);
                    } else {
                        return this.registry.get(creatorParameter.type).decode(reader, decoderContext);
                    }
                }));
            } catch (AttributeNotMapped e) {
                //TODO config if we should always ignore unmapped or throw exception
                reader.skipValue();
            }
        }
        reader.readEndDocument();
        return creator.newInstance(getInstanceAttributes());
    }

    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }

    @Override
    public Class<Object> getEncoderClass() {
        return null;
    }

    private Map<Constructor<?>, Parameters> getInstanceAttributes() {
        try {
            Map<Constructor<?>, Parameters> result = new HashMap<>();
            if (Modifier.isAbstract(creator.type.getModifiers()) || creator.type.isInterface()) {
                Set<Constructor<?>> constructors = creator.instanceAttributes.keySet();
                for (Constructor<?> constructor : constructors) {
                    Parameters parameters1 = creator.instanceAttributes.get(constructor);
                    List<Optional<? extends Class<?>>> type = parameters1.getIndexedParameters().values().stream()
                            .filter(creatorParameter -> creatorParameter.value() != null)
                            .filter(creatorParameter -> creatorParameter.name.equals("type"))
                            .map(creatorParameter -> Arrays.stream(creator.type.getAnnotation(JsonSubTypes.class)
                                    .value())
                                    .filter(type1 -> type1.name().equals(String.valueOf(creatorParameter.value())))
                                    .map(JsonSubTypes.Type::value)
                                    .findFirst())
                            .collect(Collectors.toList());
                    if (type.size() > 0) {
                        result.put(constructor, parameters1);
                        break;
                    }
                }
            } else {
                Constructor<?> constructor = creator.instanceAttributes.keySet().stream().findFirst().get();
                result.put(constructor, creator.instanceAttributes.get(constructor));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
