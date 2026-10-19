package dev.m4nd3l.processor.utility.generators;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public class CsvFormatGenerator implements FormatGenerator {
    @Override
    public JCTree.JCClassDecl generate(Context context) {
        String code = """
            package dev.m4nd3l.classutils;
            
            import com.fasterxml.jackson.annotation.JsonAutoDetect;
            import com.fasterxml.jackson.annotation.PropertyAccessor;
            import com.fasterxml.jackson.databind.ObjectMapper;
            import com.fasterxml.jackson.databind.ObjectWriter;
            import com.fasterxml.jackson.databind.ser.FilterProvider;
            import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
            import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
            import com.fasterxml.jackson.annotation.JsonFilter;
            import com.fasterxml.jackson.dataformat.csv.CsvMapper;
            import com.fasterxml.jackson.dataformat.csv.CsvSchema;
            import dev.m4nd3l.utils.ToStringType;
            import java.util.List;
            import java.util.HashSet;
            import java.util.Set;
            
            public final class JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass_CSV {
                @JsonFilter("dynamicFilter")
                private interface DynamicFilterMixIn {}
                
                private static final CsvMapper CSV_MAPPER = (CsvMapper) createMapper(new CsvMapper());
                
                private JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass_CSV() {}
                
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
                        
                        if (toSerialize != null && !toSerialize.isEmpty()) {
                            CsvSchema.Builder builder = CsvSchema.builder();
                            for (String fieldName : toSerialize) {
                                builder.addColumn(fieldName);
                            }
                            CsvSchema schema = builder.setUseHeader(true).build();
                            return CSV_MAPPER.writer(schema).writeValueAsString(targetObject);
                        }
                        
                        if (writer != null) return writer.writeValueAsString(targetObject);
                        return String.valueOf(targetObject);
                    } catch (Exception exception) {
                        return targetObject.getClass().getSimpleName() + "{serialization_error=" + exception.getMessage() + "}";
                    }
                }
                
                private static ObjectWriter getWriter(ToStringType type, boolean compact, FilterProvider filters) {
                    CsvMapper mapper = CSV_MAPPER;
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