package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.codec.util.Encoder;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PolymorphicDomainModelCodec implements Codec<Object> {
    private final CodecRegistry registry;
    private final CompositeBuilderSpec builderSpec;
    private final Encoder encoder;

    public PolymorphicDomainModelCodec(CodecRegistry registry, CompositeBuilderSpec builderSpec) {
        this.registry = registry;
        this.builderSpec = builderSpec;
        this.encoder = new Encoder(registry);
    }

    @Override
    public Object decode(BsonReader reader, DecoderContext decoderContext) {
        List<BsonToken> bsonTokens = new ArrayList<>();
        reader.readStartDocument();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            BsonToken item = new BsonToken(reader.readName(), reader.getCurrentBsonType());
            Optional<Class<?>> aClass = this.builderSpec.typeFor(item.name);
            if (aClass.isPresent()) {
                Class<?> aClass1 = aClass.get();
                Object value = this.registry.get(aClass1).decode(reader, decoderContext);
                bsonTokens.add(item.withValue(value));
            } else {
                reader.skipValue();
            }
        }

        reader.readEndDocument();
        return builderSpec.builder(bsonTokens).build();
    }

    @Override
    public void encode(BsonWriter writer, Object value, EncoderContext encoderContext) {
        this.encoder.encode(writer, value, encoderContext);
    }

    @Override
    public Class<Object> getEncoderClass() {
        return null;
    }

    static class BsonToken {

        public final String name;
        public final BsonType bsonType;
        public final Object value;

        public BsonToken(String readName, BsonType readBsonType) {
            this.name = readName;
            this.bsonType = readBsonType;
            this.value = null;
        }

        private BsonToken(String name, BsonType bsonType, Object value) {
            this.name = name;
            this.bsonType = bsonType;
            this.value = value;
        }

        public BsonToken withValue(Object value) {
            return new BsonToken(name, bsonType, value);
        }
    }
}
