package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.enumcodec.EnumCodecProvider;
import br.com.goclip.reflectcodec.model.*;
import com.mongodb.MongoClient;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BasicOutputBuffer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CodecTest {

    abstract class DescribeCodecClasses {
        <T> void writeReadCompare(T source, Codec<T> codec) {
            BasicOutputBuffer bsonOutput = new BasicOutputBuffer();
            BsonBinaryWriter writer = new BsonBinaryWriter(bsonOutput);
            codec.encode(writer, source, EncoderContext.builder().build());
            writer.close();

            BsonBinaryReader reader = new BsonBinaryReader(
                    ByteBuffer.wrap(bsonOutput.toByteArray()));
            T readNow = codec.decode(reader, DecoderContext.builder().build());

            assertThat(readNow).isEqualTo(source);
        }

        <T> void writeReadCompareInverted(T source, Codec<T> codec) {
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

            assertThat(readNow).isNotEqualTo(source);
        }

        <T> DomainModelCodec givenCodec(Class<T> tClass) {
            BuilderSpecCache builderSpecCache = new BuilderSpecCache("");
            CodecRegistry codecRegistry1 = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(new EnumCodecProvider()));
            return new DomainModelCodec(codecRegistry1,
                    builderSpecCache.createSpec(tClass));
        }

    }

    @Nested
    public class WhenClassHasPrimitiveParameters extends DescribeCodecClasses {

        @Test
        public void shouldDecodeCorrectly() {
            writeReadCompare(new PrimitivePojo(12, 'a', 12L, 12.33, 12.33f, (byte) 1), givenCodec(PrimitivePojo.class));
        }

    }

    @Nested
    public class WhenClassHasEnumParameter extends DescribeCodecClasses {

        @Test
        public void shouldDecodeCorrectly() {
            writeReadCompare(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1), givenCodec(PojoWithEnum.class));
        }

    }

    @Nested
    public class WhenClassHasCollectionParameter extends DescribeCodecClasses {

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

            List<PojoWithEnum> pojoWithEnums = new ArrayList<>();
            pojoWithEnums.add(new PojoWithEnum("lol", PojoWithEnum.TestEnum.VALUE_1));
            pojoWithEnums.add(new PojoWithEnum("lol2", PojoWithEnum.TestEnum.VALUE_2));

            writeReadCompare(new PojoWithCollection(strings, strings1, strings2, aQueue, null), givenCodec(PojoWithCollection.class));
        }

    }

    @Nested
    public class WhenClassHasCollectionParameterWithEnum extends DescribeCodecClasses {

        @Test
        public void shouldDecodeCorrectly() {
            List<PojoWithEnumList.TestEnum> enums = new ArrayList<PojoWithEnumList.TestEnum>() {{
                add(PojoWithEnumList.TestEnum.VALUE_1);
                add(PojoWithEnumList.TestEnum.VALUE_1);
            }};

            writeReadCompare(new PojoWithEnumList("aname", enums), givenCodec(PojoWithEnumList.class));
        }

    }

    @Nested
    public class WhenClassHasTransientParameter extends DescribeCodecClasses {

        @Test
        public void shouldDecodeCorrectly() {
            writeReadCompareInverted(new PojoWithTransient("test"), givenCodec(PojoWithTransient.class));
        }

    }

}
