package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.model.PojoWithCollection;
import br.com.goclip.reflectcodec.model.PojoWithEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NewInstanceTest {

    private Creator creator;

    @BeforeEach
    void test() {
        this.creator = CreatorFactory.createSingle(PojoWithCollection.class);
        this.creator
                .withValue("strings", Set.of("setstring1"))
                .withValue("stringList", List.of("liststring1"))
                .withValue("concreteList", new LinkedList<>(List.of("linkedstring1")))
                .withValue("aQueue", new LinkedList<>(List.of("aa", "bb")))
                .withValue("complexList", List.of(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1)));
    }

    @Nested
    public class WhenCreatingANewInstance {

        @Test
        void itShouldInstantiateCorrectly() {
            assertThat(creator.newInstance())
                    .isInstanceOf(PojoWithCollection.class);
        }

    }

}
