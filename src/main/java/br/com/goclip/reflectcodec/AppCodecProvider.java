package br.com.goclip.reflectcodec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Modifier;

/**
 * Created by paulo on 10/06/17.
 */
public class AppCodecProvider implements CodecProvider {

    private final ObjectSpecCache cache;

    public AppCodecProvider(String packageName) {
        cache = new ObjectSpecCache(packageName);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!Enum.class.isAssignableFrom(clazz) && cache.hasPackageName(clazz)) { //skip enums even if in the same pkg
            int modifiers = clazz.getModifiers();
            if (!needsPolymorphism(modifiers)) {
                return (Codec<T>) new DomainModelCodec(registry, cache.get(clazz));
            } else {
                return (Codec<T>) new PolymorphicDomainModelCodec(registry, cache.getComposite(clazz));
            }
        }
        return null;
    }

    private boolean needsPolymorphism(int modifiers) {
        return Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers);
    }

}
