package pets.interfaces;

import pets.enums.Breed;
import pets.enums.PetEvent;
import pets.enums.PetSize;
import pets.enums.Sex;

public interface PetFactoryWithName {
    Pet createPetWithNameEvent(String name, Breed breed, Sex sex, String petColor, PetSize petSize, String place, PetEvent event);
}
