package hr.tvz.trackmydog.localDB;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = Database.class)
public class Token extends BaseModel {

    @Column
    @PrimaryKey
    private int id;
    @Column private String token; // firebase

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        String str = "";
        str += " id: " + id;
        str += " token: " + token;
        return str;
    }
}
