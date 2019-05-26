package br.com.goclip.reflectcodec.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class PojoWithCollection {
    public final Set<String> strings;
    public final List<String> stringList;
    public final LinkedList<String> concreteList;
    public final Queue<String> aQueue;
    private final List<PojoWithEnum> complexList;

    @JsonCreator
    public PojoWithCollection(@JsonProperty("strings") Set<String> strings,
                              @JsonProperty("stringList") List<String> stringList,
                              @JsonProperty("concreteList") LinkedList<String> concreteList,
                              @JsonProperty("aQueue") Queue<String> aQueue,
                              @JsonProperty("complexList") List<PojoWithEnum> complexList) {
        this.strings = strings;
        this.stringList = stringList;
        this.concreteList = concreteList;
        this.aQueue = aQueue;
        this.complexList = complexList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PojoWithCollection that = (PojoWithCollection) o;

        if (!Objects.equals(strings, that.strings)) return false;
        if (!Objects.equals(stringList, that.stringList)) return false;
        if (!Objects.equals(concreteList, that.concreteList)) return false;
        return Objects.equals(aQueue, that.aQueue);
    }

    @Override
    public int hashCode() {
        int result = strings != null ? strings.hashCode() : 0;
        result = 31 * result + (stringList != null ? stringList.hashCode() : 0);
        result = 31 * result + (concreteList != null ? concreteList.hashCode() : 0);
        result = 31 * result + (aQueue != null ? aQueue.hashCode() : 0);
        return result;
    }
}
