package hr.tvz.trackmydog.models.mappers;

import hr.tvz.trackmydog.models.forms.DogForm;
import hr.tvz.trackmydog.models.userModel.DogInfo;

public class DogMapper {

    public static DogForm mapBasicDogToForm(DogInfo dog) {
        DogForm form = new DogForm();

        form.setKey(dog.getKey());
        form.setIndex(dog.getIndex());
        form.setName(dog.getName());
        form.setColor(dog.getColor());

        form.setBreed(dog.getBreed());
        form.setAge(dog.getAge());
        form.setPhotoURL(dog.getPhotoURL());

        form.setHeight(dog.getHeight());
        form.setWeight(dog.getWeight());
        form.setGender(dog.getGender());

        return form;
    }

}
