package pets.classes;

import pets.enums.*;
import pets.interfaces.*;

public class Dog implements Pet {

    private String name;
    private Breed breed;
    private String petColor;
    private PetSize petSize;
    private String place;
    private PetEvent event;
    private Sex sex;
    private String path;

    private Dog(String name, Breed breed, Sex sex, String petColor, PetSize petSize, String place, PetEvent event) {
        this.name = name;
        this.breed = breed;
        if (sex == null) this.sex = Sex.UNKNOWN;
        else  this.sex = sex;
        this.petColor = petColor;
        this.petSize = petSize;
        this.place = place;
        this.event = event;
    }

    private Dog(Breed breed, Sex sex, String petColor, PetSize petSize, String place, PetEvent event) {
        this.breed = breed;

        if (sex == null) this.sex = Sex.UNKNOWN;
        else  this.sex = sex;

        this.petColor = petColor;
        this.petSize = petSize;
        this.place = place;
        this.event = event;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Breed getBreed() {
        return breed;
    }

    @Override
    public String getColor() {
        return petColor;
    }

    @Override
    public PetSize getSize() {
        return petSize;
    }

    @Override
    public String getPlace() {
        return place;
    }

    @Override
    public PetEvent getEvent() {
        return event;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Собака: Кличка: " + name + ", Порода: " + breed + ", Расцветка: " + petColor + ", Размер: " + petSize + ", Район,координаты: " + place + ", Событие: " + event;
    }

    public static PetFactoryWithName petFactoryWithName = Dog::new;
    public static AnonymousPetFactory anonymousPetFactory = Dog::new;
}
