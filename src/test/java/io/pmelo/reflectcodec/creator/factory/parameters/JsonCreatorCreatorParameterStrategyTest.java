package io.pmelo.reflectcodec.creator.factory.parameters;

import io.pmelo.reflectcodec.creator.CreatorParameter;
import io.pmelo.reflectcodec.model.PojoWithCollection;
import io.pmelo.reflectcodec.model.PojoWithCollectionMissingAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class JsonCreatorCreatorParameterStrategyTest {

    @Nested
    class WhenReadingCorrectlyDeclaredJsonCreator {

        @Test
        void itShouldCreateInstanceCorrectly() {
            Constructor<?> declaredConstructor = PojoWithCollection.class.getDeclaredConstructors()[0];
            List<CreatorParameter> creatorParameters =
                    new JsonCreatorCreatorParameterStrategy().extractCreatorParameters(declaredConstructor);
            assertThat(creatorParameters).isNotEmpty();
        }

    }

    @Nested
    class WhenReadingJsonCreatorWithMissingJsonPropertyAnnotation {

        @Test
        void itShouldThrowException() {
            Constructor<?> declaredConstructor = PojoWithCollectionMissingAnnotation.class.getDeclaredConstructors()[0];
            JsonCreatorCreatorParameterStrategy jsonCreatorCreatorParameterStrategy = new JsonCreatorCreatorParameterStrategy();

            assertThatThrownBy(() -> {
                        jsonCreatorCreatorParameterStrategy.extractCreatorParameters(declaredConstructor);
                    })
                    .hasMessage("Parameter at position 1 declared on class " +
                            "PojoWithCollectionMissingAnnotation is not annotated with @JsonProperty.");
        }

    }

}
