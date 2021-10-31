package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.factory.CreatorFactory;
import io.pmelo.reflectcodec.model.PojoWithCollection;
import io.pmelo.reflectcodec.model.PojoWithEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NewInstanceTest {

    private Creator creator;
    Parameters parameters;


    @Nested
    abstract class DescribeNewInstance {

        Object result;

        @BeforeEach
        void test() {
            setup();
            result = creator.newInstance(null);
        }

        void setup() {}

    }

    @Nested
    @Disabled
    public class WhenCreatingANewInstance extends DescribeNewInstance {

        @Test
        void itShouldInstantiateCorrectly() {
            assertThat(result).isInstanceOf(PojoWithCollection.class);
        }

        public void setup() {
            creator = CreatorFactory.create(PojoWithCollection.class);
            parameters = creator.parameters();
            parameters.assignValue("strings", par -> Set.of("setstring1"));
            parameters.assignValue("stringList", par -> List.of("liststring1"));
            parameters.assignValue("concreteList", par -> new LinkedList<>(List.of("linkedstring1")));
            parameters.assignValue("aQueue", par -> new LinkedList<>(List.of("aa", "bb")));
            parameters.assignValue("complexList", par -> List.of(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1)));
        }
    }
}
