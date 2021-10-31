package io.pmelo.reflectcodec;

import io.pmelo.reflectcodec.creator.CreatorProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by paulo on 10/06/17.
 */
public class AppCodecProvider implements CodecProvider {

    private final CreatorProvider creatorProvider;

    public AppCodecProvider(String packageName) {
        creatorProvider = new CreatorProvider(packageName);
    }

    /***
     * Returns a codec for clazz if it's not an Enum and the class is in the package defined during initialization
     * @param clazz represent a class to be encoded/decoded
     * @param registry
     * @param <T> Codec and class type
     * @return codec to clazz
     */
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!Enum.class.isAssignableFrom(clazz) && creatorProvider.hasPackageName(clazz)) { //skip enums even if in the same pkg
            return (Codec<T>) new DomainModelCodec(registry, creatorProvider.get(clazz));
        }
        return null;
    }
}
