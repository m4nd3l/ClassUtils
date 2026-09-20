import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;

@Getter
@Setter
public class Person {
    private String name;
    private String secondName;
    @Getter.AlternativeNames("finalName") @Setter.AlternativeNames("finalName") private String surname;
    @Setter.Exclude @Getter.Exclude private String socialSecurityNumber;

    public Person(String name, String secondName, String surname, String socialSecurityNumber) {
        this.name = name;
        this.secondName = secondName;
        this.surname = surname;
        this.socialSecurityNumber = socialSecurityNumber;
    }
}
