package dev.m4nd3l.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility registry holding code generation templates for various popular logging frameworks.
 */
public class LoggerClasses {
    private static final Map<String, String> TEMPLATES = new HashMap<>();

    static {
        TEMPLATES.put("CommonsLog", "private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(%s.class);");
        TEMPLATES.put("Flagger", "private static final com.google.common.flogger.FluentLogger log = com.google.common.flogger.FluentLogger.forEnclosingClass();");
        TEMPLATES.put("JBossLogging", "private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(%s.class);");
        TEMPLATES.put("Log", "private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(%s.class.getName());");
        TEMPLATES.put("Log4j2", "private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(%s.class);");
        TEMPLATES.put("LoggerUtil", "private static final dev.m4nd3l.loggerutil.logger.Logger log = dev.m4nd3l.loggerutil.LoggerUtils.getLogger();");
        TEMPLATES.put("Slf4j", "private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(%s.class);");
    }

    /**
     * Retrieves the formatted code snippet string for a given logger type and target class name.
     *
     * @param loggerType the key identifying the logging framework template
     * @param className the name of the class where the logger is being injected
     * @return the formatted logger declaration code snippet, or an empty string if the type is unknown
     */
    public static String getLoggerSnippet(String loggerType, String className) {
        String template = TEMPLATES.get(loggerType);
        if (template == null) return "";
        if (template.contains("%s")) return String.format(template, className);
        return template;
    }

    /**
     * Registers a custom logger template or overrides an existing one.
     *
     * @param name the unique identifier key for the logger implementation
     * @param code the template snippet code string containing optional {@code %s} format placeholders
     */
    public static void addLogger(String name, String code) { TEMPLATES.put(name, code); }
}