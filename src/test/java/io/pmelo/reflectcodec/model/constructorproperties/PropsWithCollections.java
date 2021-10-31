package io.pmelo.reflectcodec.model.constructorproperties;

import io.pmelo.reflectcodec.model.PojoWithEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Data
@AllArgsConstructor
public class PropsWithCollections {

    public final Set<String> strings;
    public final List<String> stringList;
    public final LinkedList<String> concreteList;
    public final Queue<String> aQueue;
    public final List<PojoWithEnum> complexList;

}
