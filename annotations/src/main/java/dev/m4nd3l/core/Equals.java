package dev.m4nd3l.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Equals {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }
}
