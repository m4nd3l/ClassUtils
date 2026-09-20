package dev.m4nd3l.core;

import dev.m4nd3l.utils.ToStringType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ToString {
    ToStringType value() default ToStringType.DEFAULT;
    boolean compact() default false;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }
}
