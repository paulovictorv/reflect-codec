package br.com.goclip.reflectcodec;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by paulo on 10/06/17.
 */
public class DomainModelCodec implements Codec<Object> {
    private final CodecRegistry registry;
    private final BuilderSpec builderSpec;

    public DomainModelCodec(CodecRegistry registry, BuilderSpec builderSpec) {
        this.registry = registry;
        this.builderSpec = builderSpec;
    }

    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
        ObjectBuilder builder = builderSpec.builder();

        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();

            if (builder.hasField(fieldName)) {
                if (builder.isEnum(fieldName)) {
                    builder.mapEnumValue(fieldName, enumType -> {
                        if (reader.getCurrentBsonType() == BsonType.NULL) {
                            reader.readNull();
                            return null;
                        } else {
                            return Enum.valueOf(enumType, reader.readString());
                        }
                    });
                } else {
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
                }
            } else {
                reader.skipValue();
            }

        }
        reader.readEndDocument();

        return builder.build();
    }

    private Collection buildCollection(Class<?> type) {
        //Lets check if our type is an interface or a concrete type
        if (type.isInterface()) { //if it is an abstract type, we need to define a default implementation
            //TODO define this as an hotspot
            if (Set.class.isAssignableFrom(type)) {
                return new HashSet<>();
            } else if (Queue.class.isAssignableFrom(type)) {
                return new LinkedList<>();
            } else {
                return new ArrayList<>();
            }
        } else { //if it's a concrete type, assume it has an constructor that accepts a collection
            try {
                return (Collection) type.getConstructor().newInstance();
            } catch (Exception e) {
                //if it doesn't, we scream
                throw new RuntimeException("Unsupported Collection: " + type.getSimpleName());
            }
        }
    }

    private Class<?> mapToBoxedType(Class<?> type) {
        switch (type.getName()) {
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            default:
                return Character.class;
        }
    }

    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        Field[] fields = value.getClass().getFields();
        for (Field field : fields) {
            writer.writeName(field.getName());
            try {
                @SuppressWarnings("unchecked")
                Class<Object> type = (Class<Object>) field.getType();
                Object o = field.get(value);
                if (o == null) {
                    writer.writeNull();
                } else if (type.isEnum()) {
                    writer.writeString(o.toString());
                } else if (type.isPrimitive()) {
                    Class<Object> objectClass = (Class<Object>) mapToBoxedType(type);
                    this.registry.get(objectClass).encode(writer, o, encoderContext);
                } else {
                    this.registry.get(type).encode(writer, o, encoderContext);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        writer.writeEndDocument();
    }

    @Override
    public Class<Object> getEncoderClass() {
        return null;
    }
}
