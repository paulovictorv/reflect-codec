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
    private Parameters parameters;

    @BeforeEach
    void test() {
        this.creator = CreatorFactory.createSingle(PojoWithCollection.class);
        Parameters parameters = this.creator.parameters();

        parameters.assignValue("strings", par -> Set.of("setstring1"));
        parameters.assignValue("stringList", par -> List.of("liststring1"));
        parameters.assignValue("concreteList", par -> new LinkedList<>(List.of("linkedstring1")));
        parameters.assignValue("aQueue", par -> new LinkedList<>(List.of("aa", "bb")));
        parameters.assignValue("complexList", par -> List.of(new PojoWithEnum("name", PojoWithEnum.TestEnum.VALUE_1)));
    }

    @Nested
    public class WhenCreatingANewInstance {

        @Test
        void itShouldInstantiateCorrectly() {
            assertThat(creator.newInstance(parameters))
                    .isInstanceOf(PojoWithCollection.class);
        }

    }

}
