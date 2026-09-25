package dev.m4nd3l.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a custom logging field for the annotated class based on a specified template identifier.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CustomLog {
    /**
     * Specifies the template name or key used to generate the custom logger field.
     *
     * @return the logger template name, defaulting to "Log"
     */
    String value() default "Log";
}