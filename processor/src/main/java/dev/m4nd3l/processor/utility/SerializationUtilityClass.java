package dev.m4nd3l.processor.utility;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;

public final class SerializationUtilityClass {

    public static final String name = "dev.m4nd3l.utils.JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass";
    private static boolean created = false;

    public static void generateSerializationUtilityClass(ProcessingEnvironment processingEnv, Filer filer) {
        if (created) return;
        try {
            JavaFileObject fileObject = filer.createSourceFile("dev.m4nd3l.classutils.JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass");
            try (Writer writer = fileObject.openWriter()) {
                writer.write("""
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
                import com.fasterxml.jackson.dataformat.xml.XmlMapper;
                import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
                import com.fasterxml.jackson.dataformat.csv.CsvMapper;
                import com.fasterxml.jackson.dataformat.csv.CsvSchema;
                import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
                
                import dev.m4nd3l.utils.ToStringType;
                
                import java.util.List;
                import java.util.HashSet;
                import java.util.Set;
                
                public final class JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass {
                
                    @JsonFilter("dynamicFilter")
                    private interface DynamicFilterMixIn {}
                
                    private static final ObjectMapper JSON_MAPPER = createMapper(new ObjectMapper());
                    private static final ObjectMapper JSON_PRETTY_MAPPER = createMapper(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));
                    private static final XmlMapper XML_MAPPER = (XmlMapper) createMapper(new XmlMapper());
                    private static final XmlMapper XML_PRETTY_MAPPER = (XmlMapper) createMapper(new XmlMapper().enable(SerializationFeature.INDENT_OUTPUT));
                    private static final YAMLMapper YAML_MAPPER = (YAMLMapper) createMapper(new YAMLMapper());
                    private static final CsvMapper CSV_MAPPER = (CsvMapper) createMapper(new CsvMapper());
                    private static final JavaPropsMapper PROPERTIES_MAPPER = (JavaPropsMapper) createMapper(new JavaPropsMapper());
                
                    private JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass() {}
                
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
                
                            if (type == ToStringType.CSV && toSerialize != null && !toSerialize.isEmpty()) {
                                CsvSchema.Builder builder = CsvSchema.builder();
                                for (String fieldName : toSerialize) {
                                    builder.addColumn(fieldName);
                                }
                                CsvSchema schema = builder.setUseHeader(true).build(); // <-- Fixed here
                                return CSV_MAPPER.writer(schema).writeValueAsString(targetObject);
                            }
                
                            if (writer != null) return writer.writeValueAsString(targetObject);
                            return String.valueOf(targetObject);
                        } catch (Exception exception) {
                            return targetObject.getClass().getSimpleName() + "{serialization_error=" + exception.getMessage() + "}";
                        }
                    }
                
                    private static ObjectWriter getWriter(ToStringType type, boolean compact, FilterProvider filters) {
                        ObjectMapper mapper;
                        if (type == ToStringType.JSON) mapper = compact ? JSON_MAPPER : JSON_PRETTY_MAPPER;
                        else if (type == ToStringType.XML) mapper = compact ? XML_MAPPER : XML_PRETTY_MAPPER;
                        else if (type == ToStringType.YAML) mapper = YAML_MAPPER;
                        else if (type == ToStringType.CSV) mapper = CSV_MAPPER;
                        else if (type == ToStringType.DEFAULT) mapper = PROPERTIES_MAPPER;
                        else return null;
                
                        if (filters != null) return mapper.writer(filters);
                        return mapper.writer();
                    }
                }
                """);
                created = true;
            }
        } catch (java.io.IOException exception) { processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, exception.getMessage()); }
    }
}