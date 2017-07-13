package pl.patryk_lawicki.tasklist.listeners;

/**
 * Created by patry on 13.07.2017.
 */

public interface FirebaseChangeTaskListener {
    void onComplete();
    void onError(String message);
}
