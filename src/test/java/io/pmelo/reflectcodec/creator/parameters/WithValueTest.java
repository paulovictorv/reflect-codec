package io.pmelo.reflectcodec.creator.parameters;

import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.creator.exception.IncompatibleTypesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WithValueTest {

    private CreatorParameter string;

    @BeforeEach
    void test() {
        string = new CreatorParameter(0, String.class, null, "strings", null, null);
    }

    @Nested
    class WhenPassingMatchingTypes {

        @Test
        void itShouldThrowCorrectException() {
            CreatorParameter value = string.withValue("Value");
            assertThat(value.value())
                    .isNotNull()
                    .isInstanceOf(String.class)
                    .isEqualTo("Value");
        }

    }

    @Nested
    class WhenPassingMismatchingTypes {

        @Test
        void itShouldThrowCorrectException() {
            assertThatThrownBy(() -> string.withValue(new HashSet<String>()))
                    .isInstanceOf(IncompatibleTypesException.class)
                    .hasMessageContaining("Given HashSet is not assignable to String");
        }

    }

}
