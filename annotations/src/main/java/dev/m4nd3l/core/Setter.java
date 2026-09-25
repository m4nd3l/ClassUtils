package dev.m4nd3l.core;

import dev.m4nd3l.utils.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates setter methods for fields. When applied at the class level,
 * setters are generated for all eligible fields, allowing individual fields to be excluded.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Setter {
    /**
     * Specifies whether the method returns the class instance.
     *
     * @return if the method returns {@code this}, defaulting to {@code false}
     */
    boolean value() default false;

    /**
     * Specifies the visibility access level for the generated setter.
     *
     * @return the target access level
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;

    /**
     * Excludes a specific field from setter generation when placed on a field,
     * or excludes specific fields from class-level setter generation.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }

    /**
     * Specifies alternative names for generating additional setter methods for the target field.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface AlternativeNames {
        /**
         * Returns the alternative names used for setter method generation.
         *
         * @return an array of alternative names
         */
        String[] value();
    }
}