package tester;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("001", "John", "Doe", "Johnny", "SECRET-123");
        System.out.println("--- Testing Person ---");
        System.out.println(person.getIdentifier());
        System.out.println(person.getFirstName());
        System.out.println(person.getLastName());
        System.out.println(person.getNickname());
        System.out.println(person.getAlternativeName());
        System.out.println(person.getOtherName());

        Person modifiedPerson = person.setNickname("Jonathan");
        if (modifiedPerson != null)
            System.out.println("Setter returned itself successfully.");

        System.out.println(person);

        Vehicle vehicle = new Vehicle("Tesla", "Model 3", 4694.0, 1.0, 2.0, "INTERNAL-999");
        System.out.println("\n--- Testing Vehicle ---");
        System.out.println(vehicle);

        Configuration configuration = new Configuration("timeout", "30seconds");
        System.out.println("\n--- Testing Configuration ---");
        System.out.println(configuration);
    }
}