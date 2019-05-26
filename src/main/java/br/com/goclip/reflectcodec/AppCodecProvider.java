package br.com.goclip.reflectcodec;

import br.com.goclip.reflectcodec.creator.CreatorProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Modifier;

/**
 * Created by paulo on 10/06/17.
 */
public class AppCodecProvider implements CodecProvider {

    private final ObjectSpecCache cache;
    private final CreatorProvider creatorProvider;

    public AppCodecProvider(String packageName) {
        creatorProvider = new CreatorProvider(packageName);
        cache = new ObjectSpecCache(packageName);
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
        if (!Enum.class.isAssignableFrom(clazz) && cache.hasPackageName(clazz)) { //skip enums even if in the same pkg
            int modifiers = clazz.getModifiers();
            if (!needsPolymorphism(modifiers)) {
                return (Codec<T>) new DomainModelCodec(registry, creatorProvider.get(clazz));
            } else {
                return (Codec<T>) new PolymorphicDomainModelCodec(registry, cache.getComposite(clazz));
            }
        }
        return null;
    }


    /***
     * Verify if type is an abstract type (interface or abstract class)
     * @param modifiers a set of modifiers
     * @return boolean representing if modifiers is an interface or abstract
     */
    private boolean needsPolymorphism(int modifiers) {
        return Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers);
    }

}
