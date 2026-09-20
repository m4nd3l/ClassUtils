package dev.m4nd3l.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Getter {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface AlternativeNames { String[] value(); }
}
