package br.com.goclip.reflectcodec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by paulo on 10/06/17.
 */
public class AppCodecProvider implements CodecProvider {

    private final BuilderSpecCache cache;

    public AppCodecProvider(String packageName) {
        cache = new BuilderSpecCache(packageName);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!Enum.class.isAssignableFrom(clazz) && cache.hasPackageName(clazz)) {
            return (Codec<T>) new DomainModelCodec(registry, cache.get(clazz));
        }
        return null;
    }

}
