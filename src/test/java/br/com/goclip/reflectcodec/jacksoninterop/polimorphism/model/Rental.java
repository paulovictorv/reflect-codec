package br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model;

public abstract class Rental extends Link {
    public Rental(String id, Type type) {
        super(id, type);
    }
}
