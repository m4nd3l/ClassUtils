package dev.m4nd3l.processor.utility;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import dev.m4nd3l.processor.utility.generators.*;
import dev.m4nd3l.utils.ToStringType;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.*;

public final class SerializationUtilityClass {
    public static final Map<String, JCTree.JCClassDecl> classes = new HashMap<>();
    private static final Set<String> injectedClasses = new HashSet<>();
    private static final Map<ToStringType, FormatGenerator> generators = new EnumMap<>(ToStringType.class);

    static {
        generators.put(ToStringType.JSON, new JsonFormatGenerator());
        generators.put(ToStringType.XML, new XmlFormatGenerator());
        generators.put(ToStringType.YAML, new YamlFormatGenerator());
        generators.put(ToStringType.CSV, new CsvFormatGenerator());
        generators.put(ToStringType.DEFAULT, new DefaultFormatGenerator());
    }

    public static JCTree.JCClassDecl getOrCreateUtilityClass(Context context, ToStringType type) {
        String className = getName(type);

        if (classes.containsKey(className) && classes.get(className) != null) return classes.get(className);

        FormatGenerator generator = generators.get(type);
        if (generator == null) return null;

        JCTree.JCClassDecl classDecl = generator.generate(context);
        if (classDecl != null) classes.put(className, classDecl);

        return classDecl;
    }

    public static String getName(ToStringType type) {
        return "dev.m4nd3l.classutils.JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass_" + type.toString().toUpperCase();
    }

    public static List<JCTree.JCClassDecl> getUninjectedClasses() {
        List<JCTree.JCClassDecl> pending = new ArrayList<>();
        for (Map.Entry<String, JCTree.JCClassDecl> entry : classes.entrySet()) {
            if (!injectedClasses.contains(entry.getKey())) {
                pending.add(entry.getValue());
                injectedClasses.add(entry.getKey());
            }
        }
        return pending;
    }
}