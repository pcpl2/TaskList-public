package pl.patryk_lawicki.tasklist.listeners;

import pl.patryk_lawicki.tasklist.models.Task;

/**
 * Created by patry on 13.07.2017.
 */

public interface FirebaseLoadTasksEventListener {
    void onTaskAdded(Task task, String prevTaskUid);
    void onTaskChanged(Task task, String prevTaskUid);
    void onTaskRemoved(Task task);
    void onTaskMoved(Task task, String prevTaskUid);
    void onError(String message);
}
