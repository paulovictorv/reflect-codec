package io.pmelo.reflectcodec.codec.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Class that encapsulates common encoding logic. Since this library declares multiple codecs and all of them use the
 * same encoding logic, the code got refactored to a separate class.
 *
 */
public class Encoder {

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    private final CodecRegistry registry;

    public Encoder(CodecRegistry registry) {
        this.registry = registry;
    }

    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        for (Field field : getAllFields(value)) {
            try {
                @SuppressWarnings("unchecked")
                Class<Object> fieldType = (Class<Object>) field.getType();
                Object fieldValue = field.get(value);
                if (fieldValue != null) {
                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                    if(annotation != null) {
                        String annotationValue = annotation.value();
                        writer.writeName(annotationValue);
                    } else {
                        writer.writeName(field.getName());
                    }
                    if (fieldType.isPrimitive()) {
                        @SuppressWarnings("unchecked")
                        Class<Object> objectClass = (Class<Object>) PrimitiveUtils.mapToBoxedType(fieldType);
                        this.registry.get(objectClass).encode(writer, fieldValue, encoderContext);
                    } else {
                        this.registry.get(fieldType).encode(writer, fieldValue, encoderContext);
                    }
                }
            } catch (IllegalAccessException e) {
                logger.warn(field.getName() + " is not accessible, and can't be made accessible.", e);
            }
        }
        writer.writeEndDocument();
    }

    private List<Field> getAllFields(Object value) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> tmpClass = value.getClass();

        while (tmpClass != null) {
            fieldList.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
            tmpClass = tmpClass.getSuperclass();
        }

        return fieldList.stream()
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());
    }

}
