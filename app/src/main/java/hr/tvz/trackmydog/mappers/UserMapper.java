package hr.tvz.trackmydog.mappers;

import hr.tvz.trackmydog.forms.UserForm;
import hr.tvz.trackmydog.userModel.CurrentUser;

public class UserMapper {

    public static UserForm mapCurretUserToForm(CurrentUser user) {
        UserForm form = new UserForm();

        form.setName(user.getName());
        form.setCity(user.getCity());

        form.setPhotoURL(user.getPhotoURL());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setGender(user.getGender());

        return form;
    }

}
