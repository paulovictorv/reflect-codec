package br.com.goclip.reflectcodec.creator.factory;

import br.com.goclip.reflectcodec.creator.Creator;
import br.com.goclip.reflectcodec.creator.CreatorFactory;
import br.com.goclip.reflectcodec.creator.CreatorParameter;
import br.com.goclip.reflectcodec.model.PojoWithCollection;
import br.com.goclip.reflectcodec.model.PojoWithEnum;
import br.com.goclip.reflectcodec.model.constructorproperties.PropsWithCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSingleTest {

    abstract class DescribeCreateSingle {

        Creator creator;

        @BeforeEach
        void test() {
            this.creator = setup();
        }

        abstract Creator setup();

        abstract CreatorParameter[] expectedParameters();
    }


    @Nested
    class WhenReadingClassesWithJsonCreator {

        @Nested
        class WhenCreatingSingleWithoutGenerics extends DescribeCreateSingle {

            @Override
            Creator setup() {
                return CreatorFactory.createSingle(PojoWithEnum.class);
            }

            @Override
            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[] {
                        new CreatorParameter(0, String.class, null, "name", null),
                        new CreatorParameter(1, PojoWithEnum.TestEnum.class, null, "testEnum", null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.parameters)
                        .hasSize(2)
                        .containsExactly(expectedParameters());
            }
        }

        @Nested
        class WhenCreatingSingleWithGenerics extends DescribeCreateSingle {

            Creator setup() {
                return CreatorFactory.createSingle(PojoWithCollection.class);
            }

            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[]{
                        new CreatorParameter(0, Set.class, String.class, "strings", null),
                        new CreatorParameter(1, List.class, String.class, "stringList", null),
                        new CreatorParameter(2, LinkedList.class, String.class, "concreteList", null),
                        new CreatorParameter(3, Queue.class, String.class, "aQueue", null),
                        new CreatorParameter(4, List.class, PojoWithEnum.class, "complexList", null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.parameters)
                        .hasSize(5)
                        .containsExactly(expectedParameters());
            }

        }
    }

    @Nested
    class WhenReadingClassesWithConstructorProps {

        @Nested
        class WhenCreatingSingleWithGenerics extends DescribeCreateSingle {

            Creator setup() {
                return CreatorFactory.createSingle(PropsWithCollections.class);
            }

            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[]{
                        new CreatorParameter(0, Set.class, String.class, "strings", null),
                        new CreatorParameter(1, List.class, String.class, "stringList", null),
                        new CreatorParameter(2, LinkedList.class, String.class, "concreteList", null),
                        new CreatorParameter(3, Queue.class, String.class, "aQueue", null),
                        new CreatorParameter(4, List.class, PojoWithEnum.class, "complexList", null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.parameters)
                        .hasSize(5)
                        .containsExactly(expectedParameters());
            }

        }
    }



}
