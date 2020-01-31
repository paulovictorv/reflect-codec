package br.com.goclip.reflectcodec.creator.provider;

import br.com.goclip.reflectcodec.creator.Creator;
import br.com.goclip.reflectcodec.creator.CreatorProvider;
import br.com.goclip.reflectcodec.model.PojoWithCollection;
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
                creatorProvider = new CreatorProvider("br.com.goclip");
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
                creatorProvider = new CreatorProvider("br.com.goclip.factory");
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
