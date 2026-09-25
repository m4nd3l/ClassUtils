package dev.m4nd3l.utils;

/**
 * Defines the visibility access levels for generated builder classes, methods, and constructors,
 * mirroring standard Java access modifiers.
 */
public enum AccessLevel {

    /**
     * Accessible from anywhere.
     */
    PUBLIC,

    /**
     * Accessible within the same package and subclasses.
     */
    PROTECTED,

    /**
     * Accessible only within the same package (package-private).
     */
    PACKAGE,

    /**
     * Accessible only within the defining class.
     */
    PRIVATE
}