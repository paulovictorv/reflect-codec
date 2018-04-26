package br.com.goclip.reflectcodec;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inheritance {
    String mappedBy();
}
