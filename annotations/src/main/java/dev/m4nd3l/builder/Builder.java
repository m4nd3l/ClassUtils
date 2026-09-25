package dev.m4nd3l.builder;

import dev.m4nd3l.utils.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a comprehensive and flexible builder pattern implementation for the annotated class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {

    /**
     * Specifies alternative names for the static builder creation methods.
     *
     * @return an array of builder creation method names
     */
    String[] value() default "builder";

    /**
     * Specifies the names of the terminal build methods that construct and return the target object.
     *
     * @return an array of build method names
     */
    String[] buildMethodNames() default "build";

    /**
     * Specifies the format string used to generate the name of the inner builder class.
     * The format placeholder {@code %s} is replaced by the target class name.
     *
     * @return the pattern for the generated builder class name
     */
    String buildClassName() default "%sBuilder_ClassUtilsInternalGeneratedBuilderClass";

    /**
     * Determines whether a {@code toBuilder()} method should be generated on the target class,
     * allowing existing instances to be converted back into a builder.
     *
     * @return true if the {@code toBuilder()} method should be generated, false otherwise
     */
    boolean toBuilderMethod() default false;

    /**
     * Defines the global prefix applied to all setter methods inside the generated builder.
     *
     * @return the global setter prefix string
     */
    String setterPrefix() default "";

    /**
     * Specifies the visibility access level for the generated builder class and its methods.
     *
     * @return the target access level
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;

    /**
     * Excludes a specific field from being managed or populated by the generated builder.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Exclude { }

    /**
     * Specifies alternative names for a field, generating additional setter methods in the builder
     * so that the field can be populated using multiple different property names.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface AlternativeNames {
        /**
         * Returns the alternative names for the field.
         *
         * @return an array of alternative field names
         */
        String[] value();
    }

    /**
     * Marks a field to use its default value (defined directly in the class field initialization)
     * if no value is explicitly provided via the builder.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Default { }

    /**
     * Assigns a specific, custom access level to the setter method of a single annotated field,
     * overriding the class-level settings.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface CustomAccessLevel {
        /**
         * Returns the custom access level for the field.
         *
         * @return the access level value
         */
        AccessLevel value();
    }

    /**
     * Overrides the setter prefix for a specific field (for example, if you want a single field
     * to use a "set" prefix instead of the global default).
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface CustomSetterPrefix {
        /**
         * Returns the custom prefix string for the field setter.
         *
         * @return the custom setter prefix
         */
        String value();
    }

    /**
     * Configures custom extraction behavior when obtaining a field value from an existing instance
     * (used when {@code toBuilderMethod} is enabled).
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface ObtainVia {
        /**
         * Specifies the name of the getter or custom retrieval method used to obtain the field's value.
         *
         * @return the name of the extraction method
         */
        String method() default "";

        /**
         * Indicates whether the specified extraction method is a static utility function.
         *
         * @return true if the method is static, false if it is an instance method
         */
        boolean staticMethod() default false;
    }
}