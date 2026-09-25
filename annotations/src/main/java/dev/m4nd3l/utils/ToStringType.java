package dev.m4nd3l.utils;

/**
 * Defines the supported data serialization styles and layouts for generated {@code toString} methods.
 */
public enum ToStringType {
    /** Standard key-value pairing style format. */
    DEFAULT,
    /** JSON formatted output structure. */
    JSON,
    /** XML markup formatted output structure. */
    XML,
    /** YAML formatted output structure. */
    YAML,
    /** Comma-Separated Values formatted output structure. */
    CSV,
}