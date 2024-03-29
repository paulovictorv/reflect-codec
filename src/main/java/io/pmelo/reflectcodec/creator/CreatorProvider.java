package io.pmelo.reflectcodec.creator;

import io.pmelo.reflectcodec.creator.factory.CreatorFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CreatorProvider {

    private final String pkg;
    private final Map<Class<?>, Creator> creatorCache;

    public CreatorProvider(String pkg) {
        this.pkg = pkg;
        creatorCache = new ConcurrentHashMap<>();
    }

    public Creator get(Class<?> type) {
        if (checkPackage(type.getPackage().getName())) {
            if (creatorCache.containsKey(type)) {
                return creatorCache.get(type);
            } else {
                Creator single = CreatorFactory.create(type);
                creatorCache.put(type, single);
                return single;
            }
        } else {
            return null;
        }
    }

    public boolean hasPackageName(Class<?> cachedClass) {
        Package aPackage = cachedClass.getPackage();
        return aPackage != null && aPackage.getName().contains(pkg);
    }

    private boolean checkPackage(String provPackage) {
        return provPackage != null && provPackage.contains(pkg);
    }
}
