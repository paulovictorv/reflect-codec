package br.com.goclip.reflectcodec.creator.factory;

import br.com.goclip.reflectcodec.creator.Creator;
import br.com.goclip.reflectcodec.creator.CreatorFactory;
import br.com.goclip.reflectcodec.creator.CreatorParameter;
import br.com.goclip.reflectcodec.creator.Parameters;
import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.Link;
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

        abstract Object[] expectedParameters();
    }


    @Nested
    class WhenReadingClassesWithJsonCreator {

        @Nested
        class WhenCreatingSingleWithoutGenerics extends DescribeCreateSingle {

            @Override
            Creator setup() {
                Creator single = CreatorFactory.create(PojoWithEnum.class);
                Parameters parameters = single.subtypes.values().stream().findFirst().get().parameters;
                parameters.assignValue("name", par -> "string");
                parameters.assignValue("testEnum", par -> PojoWithEnum.TestEnum.VALUE_1);
                return null;
//                return single.withParameters(parameters);
            }

            @Override
            Object[] expectedParameters() {
                return new Object[] {
                        "string",
                        PojoWithEnum.TestEnum.VALUE_1
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.subtypes.values().stream().findFirst().get().parameters.sortedValues())
                        .isEqualTo(2)
                        .containsExactly(expectedParameters());
            }
        }

        @Nested
        class WhenCreatingSingleWithGenerics extends DescribeCreateSingle {

            Creator setup() {
                return CreatorFactory.create(PojoWithCollection.class);
            }

            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[]{
                        new CreatorParameter(0, Set.class, String.class, "strings", null, null),
                        new CreatorParameter(1, List.class, String.class, "stringList", null, null),
                        new CreatorParameter(2, LinkedList.class, String.class, "concreteList", null, null),
                        new CreatorParameter(3, Queue.class, String.class, "aQueue", null, null),
                        new CreatorParameter(4, List.class, PojoWithEnum.class, "complexList", null, null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.subtypes.values().stream().findFirst().get().parameters.getIndexedParameters().values())
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
                return CreatorFactory.create(PropsWithCollections.class);
            }

            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[]{
                        new CreatorParameter(0, Set.class, String.class, "strings", null, null),
                        new CreatorParameter(1, List.class, String.class, "stringList", null, null),
                        new CreatorParameter(2, LinkedList.class, String.class, "concreteList", null, null),
                        new CreatorParameter(3, Queue.class, String.class, "aQueue", null, null),
                        new CreatorParameter(4, List.class, PojoWithEnum.class, "complexList", null, null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.subtypes.values().stream().findFirst().get().parameters.getIndexedParameters().values())
                        .hasSize(5)
                        .containsExactly(expectedParameters());
            }
        }
    }


    @Nested
    class WhenReadingSubClasses {

        @Nested
        class WhenCreatingSingleWithGenerics extends DescribeCreateSingle {

            Creator setup() {
                return CreatorFactory.create(Link.class);
            }

            CreatorParameter[] expectedParameters() {
                return new CreatorParameter[]{
                        new CreatorParameter(0, Set.class, String.class, "strings", null, null),
                        new CreatorParameter(1, List.class, String.class, "stringList", null, null),
                        new CreatorParameter(2, LinkedList.class, String.class, "concreteList", null, null),
                        new CreatorParameter(3, Queue.class, String.class, "aQueue", null, null),
                        new CreatorParameter(4, List.class, PojoWithEnum.class, "complexList", null, null)
                };
            }

            @Test
            void itShouldListParametersInOrder() {
                assertThat(creator.subtypes.values().stream().findFirst().get().parameters.getIndexedParameters().values())
                        .hasSize(5)
                        .containsExactly(expectedParameters());
            }

        }
    }



}
