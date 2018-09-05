package hr.tvz.trackmydog.mappers;

import java.io.Serializable;

import hr.tvz.trackmydog.forms.DogForm;
import hr.tvz.trackmydog.userModel.BasicDog;

public class DogMapper implements Serializable {

    public static DogForm mapBasicDogToForm(BasicDog dog) {
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
