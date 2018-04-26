package br.com.goclip.reflectcodec;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(InheritanceMaps.class)
public @interface InheritanceMap {
    String keyValue();
    Class<?> impl();
}
