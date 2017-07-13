package pl.patryk_lawicki.tasklist.listeners;

/**
 * Created by patry on 13.07.2017.
 */

public interface FirebaseLoadTasksCountListener {
    void onComplete(long count);
    void onError(String message);
}

