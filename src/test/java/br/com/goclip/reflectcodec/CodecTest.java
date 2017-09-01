package br.com.goclip.reflectcodec;

import com.mongodb.MongoClient;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class CodecTest {


    abstract static class Describe_Codec_Classes {
        <T> void writeReadCompare(T source, Codec<T> codec) {
            BasicOutputBuffer bsonOutput = new BasicOutputBuffer();
            BsonBinaryWriter writer = new BsonBinaryWriter(bsonOutput);
            writer.writeStartDocument();
            writer.writeName("name");
            codec.encode(writer, source, EncoderContext.builder().build());
            writer.writeEndDocument();
            writer.close();

            BsonBinaryReader reader = new BsonBinaryReader(
                    ByteBuffer.wrap(bsonOutput.toByteArray()));
            reader.readStartDocument();
            assertThat(reader.readName()).isEqualTo("name");
            T readNow = codec.decode(reader, DecoderContext.builder().build());

            assertThat(readNow).isEqualTo(source);
        }

        <T> DomainModelCodec givenCodec(Class<T> tClass) {
            BuilderSpecCache builderSpecCache = new BuilderSpecCache("");
            return new DomainModelCodec(MongoClient.getDefaultCodecRegistry(),
                    builderSpecCache.createSpec(tClass));
        }

    }

    public static class When_Class_Has_Primitive_Parameters extends Describe_Codec_Classes {

        @Test
        public void shouldDecodeCorrectly() {
            writeReadCompare(new PrimitivePojo(12, 'a', 12L, 12.33, 12.33f, (byte) 1), givenCodec(PrimitivePojo.class));
        }

    }

    public static class When_Class_Has_Enum_Parameter extends Describe_Codec_Classes {

        @Test
        public void shouldDecodeCorrectly() {
            writeReadCompare(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1), givenCodec(PojoWithEnum.class));
        }

    }

    public static class When_Class_Has_Collection_Parameter extends Describe_Codec_Classes {

        @Test
        public void shouldDecodeCorrectly() {
            HashSet<String> strings = new HashSet<>();
            strings.add("aaa");
            strings.add("bbb");

            ArrayList<String> strings1 = new ArrayList<>();
            strings1.add("ae");
            strings1.add("uhul");

            LinkedList<String> strings2 = new LinkedList<>();
            strings2.add("opa");
            strings2.add("q iisso");

            Queue<String> aQueue = new LinkedList<>();
            aQueue.offer("lol");
            aQueue.offer("lol");

            writeReadCompare(new PojoWithCollection(strings, strings1, strings2, aQueue), givenCodec(PojoWithCollection.class));
        }

    }

}
