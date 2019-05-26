package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.creator.exception.AttributeNotMapped;
import br.com.goclip.reflectcodec.creator.exception.IncompatibleTypesException;
import br.com.goclip.reflectcodec.model.PojoWithCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WithValueTest {

    Creator creator;

    @BeforeEach
    void test() {
        this.creator = CreatorFactory.createSingle(PojoWithCollection.class);
    }

    @Nested
    class WhenPassingCorrectValues {

        @Test
        void itShouldSetValueCorrectly() {
            creator.withValue("strings", new HashSet<String>());
        }

    }

    @Nested
    class WhenPassingNotMappedAttributes {

        @Test
        void itShouldThrowAttrNotMappedException() {
            assertThatThrownBy(() -> creator.withValue("asasda", 2))
                    .isInstanceOf(AttributeNotMapped.class)
                    .hasMessageContaining("Field asasda not found in class PojoWithCollection.");
        }

    }

    @Nested
    class WhenPassingMismatchingTypes {

        @Test
        void itShouldThrowIncompatTypes() {
            assertThatThrownBy(() -> creator.withValue("stringList", new HashSet<String>()))
                    .isInstanceOf(IncompatibleTypesException.class)
                    .hasMessageContaining("Given HashSet is not assignable to List");
        }

    }

}
