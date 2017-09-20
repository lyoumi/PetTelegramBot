package pets.interfaces;

import pets.enums.Breed;
import pets.enums.PetEvent;
import pets.enums.PetSize;

public interface Pet {
    String getName();
    Breed getBreed();
    String getColor();
    PetSize getSize();
    String getPlace();
    PetEvent getEvent();
    String getPath();
    void setPath(String s);
}
