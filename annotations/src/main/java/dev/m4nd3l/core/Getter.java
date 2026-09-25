package dev.m4nd3l.core;

import dev.m4nd3l.utils.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates getter methods for fields. When applied at the class level,
 * getters are generated for all eligible fields, allowing individual fields to be excluded.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Getter {
    /**
     * Specifies the visibility access level for the generated getter.
     *
     * @return the target access level
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;

    /**
     * Excludes a specific field from getter generation when placed on a field,
     * or excludes specific fields from class-level getter generation.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }

    /**
     * Specifies alternative names for generating additional getter methods for the target field.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface AlternativeNames {
        /**
         * Returns the alternative names used for getter method generation.
         *
         * @return an array of alternative names
         */
        String[] value();
    }
}