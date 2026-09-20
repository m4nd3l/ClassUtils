public class Main {
    public static void main(String[] args) {
        Person person = new Person("Alice", "B.", "Smith", "123-45-6789");

        // Test standard getter and setter
        person.setName("Alicia");
        System.out.println(person.getName()); // Should print: Alicia

        // Test alternative name injection (@Getter.AlternativeNames("finalName"))
        person.setFinalName("Johnson");
        System.out.println(person.getFinalName()); // Should print: Johnson

        // Verify exclusion (@Setter.Exclude / @Getter.Exclude)
        // person.getSocialSecurityNumber(); // <-- This line should cause a compile-time error if exclusion works!
    }
}