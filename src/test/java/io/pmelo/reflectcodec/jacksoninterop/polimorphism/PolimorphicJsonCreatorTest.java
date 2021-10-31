package io.pmelo.reflectcodec.jacksoninterop.polimorphism;

import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.Link;
import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.Person;
import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.SimpleRental;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PolimorphicJsonCreatorTest {

    class DescribePolimorphicTest {

        String simpleRentalJson;
        Link rental;

        @BeforeEach
        public void unmarshall() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SimpleRental simpleRental = new SimpleRental("123", new Person(null, "123"));
            simpleRentalJson = objectMapper.writeValueAsString(simpleRental);
            rental = objectMapper.readValue(simpleRentalJson, Link.class);
        }

    }

    @Nested
    class WhenUnmarshallingToAbstractClass extends DescribePolimorphicTest {

        @Test
        void itShouldUnmarshallCorrectly() {
            assertThat(rental)
                    .isInstanceOf(SimpleRental.class);
        }

    }

}
