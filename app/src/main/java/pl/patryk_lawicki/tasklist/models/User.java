package pl.patryk_lawicki.tasklist.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by patry on 08.05.2017.
 */

@IgnoreExtraProperties
public class User {
    @Exclude
    private String uid;

    private String name;
    private String email;

    public User() {
    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
