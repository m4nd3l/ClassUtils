package dev.m4nd3l.core;

import dev.m4nd3l.utils.ToStringType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a {@code toString} method for the annotated class, supporting various output formats and styling structures.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ToString {
    /**
     * Specifies the formatting structure style for the generated {@code toString} output.
     *
     * @return the string format type, defaulting to {@link ToStringType#DEFAULT}
     */
    ToStringType value() default ToStringType.DEFAULT;

    /**
     * Specifies whether the generated {@code toString} output should be compact.
     *
     * @return {@code true} if output should be compact, {@code false} otherwise
     */
    boolean compact() default false;

    /**
     * Excludes a specific field from being included in the generated {@code toString} output.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }
}