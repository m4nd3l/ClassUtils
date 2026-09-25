package tester;

import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.utils.ToStringType;

@Getter
@Setter(true)
@ToString(value = ToStringType.JSON, compact = true)
public class Person {
    private String identifier;
    private String firstName;
    private String lastName;

    @Getter.AlternativeNames({"alternativeName", "otherName"})
    @Setter.AlternativeNames({"alternativeName", "otherName"})
    private String nickname;

    @Setter.Exclude
    @Getter.Exclude
    private String confidentialNumber;

    public Person(String identifier, String firstName, String lastName, String nickname, String confidentialNumber) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.confidentialNumber = confidentialNumber;
    }
}