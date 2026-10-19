package dev.m4nd3l.processor.utility.generators;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public class JsonFormatGenerator implements FormatGenerator {
    @Override
    public JCTree.JCClassDecl generate(Context context) {
        String code = """
            package dev.m4nd3l.classutils;
            
            import com.fasterxml.jackson.annotation.JsonAutoDetect;
            import com.fasterxml.jackson.annotation.PropertyAccessor;
            import com.fasterxml.jackson.databind.ObjectMapper;
            import com.fasterxml.jackson.databind.SerializationFeature;
            import com.fasterxml.jackson.databind.ObjectWriter;
            import com.fasterxml.jackson.databind.ser.FilterProvider;
            import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
            import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
            import com.fasterxml.jackson.annotation.JsonFilter;
            import dev.m4nd3l.utils.ToStringType;
            import java.util.List;
            import java.util.HashSet;
            import java.util.Set;
            
            public final class JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass_JSON {
                @JsonFilter("dynamicFilter")
                private interface DynamicFilterMixIn {}
                
                private static final ObjectMapper JSON_MAPPER = createMapper(new ObjectMapper());
                private static final ObjectMapper JSON_PRETTY_MAPPER = createMapper(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));
                
                private JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass_JSON() {}
                
                private static ObjectMapper createMapper(ObjectMapper mapper) {
                    mapper.addMixIn(Object.class, DynamicFilterMixIn.class);
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    return mapper;
                }
                
                public static String serialize(Object targetObject, ToStringType type, boolean compact, List<String> toSerialize) {
                    if (targetObject == null) return "null";
                    try {
                        ObjectWriter writer;
                        if (toSerialize == null || toSerialize.isEmpty()) writer = getWriter(type, compact, null);
                        else {
                            Set<String> propertySet = new HashSet<>(toSerialize);
                            FilterProvider filters = new SimpleFilterProvider().addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(propertySet));
                            writer = getWriter(type, compact, filters);
                        }
                        if (writer != null) return writer.writeValueAsString(targetObject);
                        return String.valueOf(targetObject);
                    } catch (Exception exception) {
                        return targetObject.getClass().getSimpleName() + "{serialization_error=" + exception.getMessage() + "}";
                    }
                }
                
                private static ObjectWriter getWriter(ToStringType type, boolean compact, FilterProvider filters) {
                    ObjectMapper mapper = compact ? JSON_MAPPER : JSON_PRETTY_MAPPER;
                    if (filters != null) return mapper.writer(filters);
                    return mapper.writer();
                }
            }
            """;
        return parseClass(context, code);
    }

    private JCTree.JCClassDecl parseClass(Context context, String code) {
        ParserFactory parserFactory = ParserFactory.instance(context);
        JavacParser parser = parserFactory.newParser(code, true, true, true);
        JCTree.JCCompilationUnit unit = parser.parseCompilationUnit();
        for (JCTree def : unit.defs) {
            if (def instanceof JCTree.JCClassDecl classDecl) return classDecl;
        }
        return null;
    }
}