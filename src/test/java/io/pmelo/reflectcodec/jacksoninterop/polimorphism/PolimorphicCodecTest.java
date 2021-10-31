package io.pmelo.reflectcodec.jacksoninterop.polimorphism;

import io.pmelo.reflectcodec.DescribeCodecClasses;
import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.Link;
import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.Person;
import io.pmelo.reflectcodec.jacksoninterop.polimorphism.model.SimpleRental;
import org.bson.io.BasicOutputBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PolimorphicCodecTest {

    abstract class DescribePolimorphicTest<T> extends DescribeCodecClasses {
        T read;

        protected abstract BasicOutputBuffer write();
        protected abstract T read(BasicOutputBuffer buffer);

        @BeforeEach
        protected void encoding() {
            read = read(write());
        }
    }

    @Nested
    class WhenProvidingTopmostClass extends DescribePolimorphicTest<Link> {

        @Override
        protected BasicOutputBuffer write() {
            return write(new SimpleRental("123", new Person(null, "name")), Link.class);
        }

        @Override
        protected Link read(BasicOutputBuffer buffer) {
            return read(buffer, Link.class);
        }

        @Test
        void itShouldDecodeToSimpleRental() {
            assertThat(read)
                    .isInstanceOf(SimpleRental.class);
        }

    }

    @Nested
    class WhenProvidingMiddleClass extends DescribePolimorphicTest<SimpleRental> {

        @Override
        protected BasicOutputBuffer write() {
            return write(new SimpleRental("123", new Person(null, "name")), Link.class);
        }

        @Override
        protected SimpleRental read(BasicOutputBuffer buffer) {
            return read(buffer, SimpleRental.class);
        }

        @Test
        void itShouldDecodeToSimpleRental() {
            assertThat(read).isInstanceOf(SimpleRental.class);
        }
    }

}
