package br.com.goclip.reflectcodec.util;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.goclip.reflectcodec.util.PrimitiveUtils.mapToBoxedType;

public class Encoder {

    private final CodecRegistry registry;

    public Encoder(CodecRegistry registry) {
        this.registry = registry;
    }

    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        List<Field> allFields = getAllFields(value);
        for (Field field : allFields) {
            if (!Modifier.isTransient(field.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<Object> type = (Class<Object>) field.getType();
                    Object o = field.get(value);
                    if (o != null) {
                        writer.writeName(field.getName());
                        if (type.isPrimitive()) {
                            Class<Object> objectClass = (Class<Object>) mapToBoxedType(type);
                            this.registry.get(objectClass).encode(writer, o, encoderContext);
                        } else {
                            this.registry.get(type).encode(writer, o, encoderContext);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        }
        writer.writeEndDocument();
    }

    public List<Field> getAllFields(Object value) {
        List<Field> fieldList = new ArrayList<>();
        Class tmpClass = value.getClass();
        while (tmpClass != null) {
            fieldList.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
            tmpClass = tmpClass.getSuperclass();
        }

        return fieldList;
    }

}
