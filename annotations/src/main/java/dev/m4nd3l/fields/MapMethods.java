package dev.m4nd3l.fields;

import dev.m4nd3l.utils.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates map helper methods (such as put or entry addition helpers) for map fields.
 * When applied at the class level, it targets all maps in the class, allowing specific maps to be excluded.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface MapMethods {
    /**
     * Specifies the visibility access level for the generated methods.
     *
     * @return the target access level
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;

    /**
     * Excludes a specific map field from method generation when placed on a field,
     * or excludes specific maps from class-level generation when using class-level annotations.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }

    /**
     * Specifies alternative names for generating additional helper methods for the target map field.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface AlternativeNames {
        /**
         * Returns the alternative names used for method generation.
         *
         * @return an array of alternative names
         */
        String[] value();
    }
}