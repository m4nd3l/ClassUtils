package tester;

import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.utils.ToStringType;
import dev.m4nd3l.utils.AccessLevel;

@ToString(value = ToStringType.YAML, compact = true)
public class Configuration {
    @Getter(accessLevel = AccessLevel.PUBLIC)
    @Setter(value = true, accessLevel = AccessLevel.PUBLIC)
    private String propertyKey;

    @Getter(accessLevel = AccessLevel.PROTECTED)
    @Setter(value = false, accessLevel = AccessLevel.PROTECTED)
    private String propertyValue;

    public Configuration(String propertyKey, String propertyValue) {
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }
}