package pl.patryk_lawicki.tasklist.listeners;

import pl.patryk_lawicki.tasklist.models.User;

/**
 * Created by patry on 13.07.2017.
 */

public interface FirebaseLoadUserListener {
    void onCompleteExist(User user);
    void OnCompleteNotExist();
    void onError(String message);
}
