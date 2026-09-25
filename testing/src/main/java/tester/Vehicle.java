package tester;

import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.utils.ToStringType;

@Getter
@Setter(false)
@ToString(value = ToStringType.XML, compact = false)
public class Vehicle {
    private String brand;
    private String model;
    private double dimension;
    private double radius;
    private double diameter;

    @ToString.Exclude
    private String internalIdentifier;

    public Vehicle(String brand, String model, double dimension, double radius, double diameter, String internalIdentifier) {
        this.brand = brand;
        this.model = model;
        this.dimension = dimension;
        this.radius = radius;
        this.diameter = diameter;
        this.internalIdentifier = internalIdentifier;
    }
}