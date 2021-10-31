package io.pmelo.reflectcodec.creator.provider;

import io.pmelo.reflectcodec.creator.Creator;
import io.pmelo.reflectcodec.creator.CreatorProvider;
import io.pmelo.reflectcodec.model.PojoWithCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetCreatorTest {

    @Nested
    class WhenGettingCreatorsInWatchedPackage {

        private CreatorProvider creatorProvider;
        private Creator creator;

        @Nested
        class InWatchedPackage {

            @BeforeEach
            void setup() {
                creatorProvider = new CreatorProvider("io.pmelo");
                creator = creatorProvider.get(PojoWithCollection.class);
            }

            @Test
            void itShouldGenerateCreator() {
                assertThat(creator.type)
                        .isEqualTo(PojoWithCollection.class);
            }

            @Test
            void itShouldUseCache() {
                assertThat(creator)
                        .matches(c -> c == creator);
            }
        }

        @Nested
        class InOuterPackages {

            @BeforeEach
            void setup() {
                creatorProvider = new CreatorProvider("io.pmelo.factory");
                creator = creatorProvider.get(PojoWithCollection.class);
            }

            @Test
            void itShouldReturnNull() {
                assertThat(creator)
                        .isNull();
            }

        }

    }

}
