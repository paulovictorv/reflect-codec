package io.pmelo.reflectcodec;

import com.mongodb.MongoClientSettings;
import io.pmelo.reflectcodec.enumcodec.EnumCodecProvider;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BasicOutputBuffer;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DescribeCodecClasses {

    protected <T> BasicOutputBuffer write(T source, Class<T> tClass) {
        BasicOutputBuffer bsonOutput = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(bsonOutput);
        codec(tClass).encode(writer, source, EncoderContext.builder().build());
        writer.close();
        return bsonOutput;
    }

    protected <T> T read(BasicOutputBuffer buffer, Class<T> tClass) {
        BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(buffer.toByteArray()));
        return codec(tClass).decode(reader, DecoderContext.builder().build());
    }

    protected <T> void writeReadCompare(T source, Codec<T> codec) {
        BasicOutputBuffer bsonOutput = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(bsonOutput);
        codec.encode(writer, source, EncoderContext.builder().build());
        writer.close();

        BsonBinaryReader reader = new BsonBinaryReader(
                ByteBuffer.wrap(bsonOutput.toByteArray()));
        T readNow = codec.decode(reader, DecoderContext.builder().build());

        assertThat(readNow).isEqualTo(source);
    }

    protected <T> void readWriteCompare(T source, Codec<T> codec) {
        BasicOutputBuffer bsonOutput = new BasicOutputBuffer();
        BsonBinaryWriter writer = new BsonBinaryWriter(bsonOutput);
        writer.writeStartDocument();
        writer.writeName("name");
        codec.encode(writer, source, EncoderContext.builder().build());
        writer.writeEndDocument();
        writer.close();

        BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(bsonOutput.toByteArray()));
        reader.readStartDocument();
        assertThat(reader.readName()).isEqualTo("name");
        T readNow = codec.decode(reader, DecoderContext.builder().build());

        assertThat(readNow).isNotEqualTo(source);
    }

    protected <T> Codec<T> codec(Class<T> tClass) {

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new EnumCodecProvider(),
                        new AppCodecProvider("io.pmelo.reflectcodec")));
        return codecRegistry.get(tClass);
    }

}
