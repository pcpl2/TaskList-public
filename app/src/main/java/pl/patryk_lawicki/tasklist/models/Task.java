package pl.patryk_lawicki.tasklist.models;

import com.google.firebase.database.Exclude;

/**
 * Created by patry on 09.05.2017.
 */

public class Task {
    @Exclude
    private String uid;

    private boolean complete;
    private String name;
    private long created;

    public Task() {
    }

    public Task(boolean complete, String name, Long created) {
        this.complete = complete;
        this.name = name;
        this.created = created;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Exclude
    public boolean equalsData(Task obj) {
        return (this.uid.equals(obj.uid)) && (this.name.equals(obj.name)) && (this.complete == obj.complete);
    }

}
