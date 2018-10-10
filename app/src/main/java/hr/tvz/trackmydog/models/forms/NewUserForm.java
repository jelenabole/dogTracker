package hr.tvz.trackmydog.models.forms;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class NewUserForm implements Serializable {

    private String email;
    private String token;
    private boolean follow;

    public NewUserForm(String email, String token) {
        this.email = email;
        this.token = token;
        this.follow = true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    @Override
    public String toString() {
        String str = "********** \n";

        str += " - " + "email: " + email;
        str += "\n - " + "token: " + token;
        str += "\n - " + "follow: " + follow;

        return str;
    }
}
