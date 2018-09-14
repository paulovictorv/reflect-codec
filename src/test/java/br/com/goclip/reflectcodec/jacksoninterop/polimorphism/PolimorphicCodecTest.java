package br.com.goclip.reflectcodec.jacksoninterop.polimorphism;

import br.com.goclip.reflectcodec.DescribeCodecClasses;
import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.Link;
import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.Person;
import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.SimpleRental;
import org.bson.io.BasicOutputBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PolimorphicCodecTest {

    abstract class DescribePolimorphicTest extends DescribeCodecClasses {

        protected Link read;

        abstract Link subject();
        abstract Class<Link> theClass();

        @BeforeEach
        protected void encoding() {
            BasicOutputBuffer write = write(subject(), theClass());
            read = read(write, theClass());
        }
    }

    @Nested
    class WhenProvidingTopmostClass extends DescribePolimorphicTest {

        @Override
        Link subject() {
            return new SimpleRental("123", new Person("name"));
        }

        @Override
        Class<Link> theClass() {
            return Link.class;
        }

        @Test
        void itShouldDecodeToSimpleRental() {
            assertThat(read)
                    .isInstanceOf(SimpleRental.class);
        }

    }

}
