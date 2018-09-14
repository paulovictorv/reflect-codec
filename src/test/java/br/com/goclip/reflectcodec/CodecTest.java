package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.jacksoninterop.creators.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CodecTest {

    @Nested
    class WhenClassHasPrimitiveParameters extends DescribeCodecClasses {

        @Test
        void itShouldDecodeCorrectly() {
            writeReadCompare(new PrimitivePojo(12, 'a', 12L, 12.33, 12.33f, (byte) 1), codec(PrimitivePojo.class));
        }

    }

    @Nested
    class WhenClassHasEnumParameter extends DescribeCodecClasses {

        @Test
        void itShouldDecodeCorrectly() {
            writeReadCompare(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1), codec(PojoWithEnum.class));
        }

    }

    @Nested
    class WhenClassHasCollectionParameter extends DescribeCodecClasses {

        @Test
        void itShouldDecodeCorrectly() {
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

            writeReadCompare(new PojoWithCollection(strings, strings1, strings2, aQueue, null), codec(PojoWithCollection.class));
        }

    }

    @Nested
    class WhenClassHasCollectionParameterWithEnum extends DescribeCodecClasses {

        @Test
        void itShouldDecodeCorrectly() {
            List<PojoWithEnumList.TestEnum> enums = new ArrayList<PojoWithEnumList.TestEnum>() {{
                add(PojoWithEnumList.TestEnum.VALUE_1);
                add(PojoWithEnumList.TestEnum.VALUE_1);
            }};

            writeReadCompare(new PojoWithEnumList("aname", enums), codec(PojoWithEnumList.class));
        }

    }

    @Nested
    class WhenClassHasTransientParameter extends DescribeCodecClasses {

        @Test
        void itShouldDecodeCorrectly() {
            readWriteCompare(new PojoWithTransient("test"), codec(PojoWithTransient.class));
        }

    }

}
