package dev.m4nd3l.all;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that all standard code generation options should be applied to the annotated class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface All {
    /**
     * Specifies the name of the logger to use.
     *
     * @return the value string, defaulting to "Log"
     */
    String value() default "Log";
}
