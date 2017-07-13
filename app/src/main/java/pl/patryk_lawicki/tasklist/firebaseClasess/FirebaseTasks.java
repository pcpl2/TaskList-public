package pl.patryk_lawicki.tasklist.firebaseClasess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.patryk_lawicki.tasklist.listeners.FirebaseChangeTaskListener;
import pl.patryk_lawicki.tasklist.listeners.FirebaseLoadTasksCountListener;
import pl.patryk_lawicki.tasklist.listeners.FirebaseLoadTasksEventListener;
import pl.patryk_lawicki.tasklist.models.Task;

/**
 * Created by patry on 13.07.2017.
 */

public class FirebaseTasks {
    private static final String TAG = FirebaseTasks.class.getSimpleName();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private DatabaseReference tasksReference = FirebaseDatabase.getInstance().getReference("Tasks").child(firebaseUser.getUid());

    private static FirebaseTasks instance = new FirebaseTasks();
    public static FirebaseTasks getInstance() {
        return instance;
    }

    public void loadTasksCountDynamic(FirebaseLoadTasksCountListener firebaseLoadTasksCountListener) {
        tasksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseLoadTasksCountListener != null) {
                    firebaseLoadTasksCountListener.onComplete(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(firebaseLoadTasksCountListener != null) {
                    firebaseLoadTasksCountListener.onError(databaseError.getMessage());
                }
            }
        });
    }

    public void loadTasksEventDynamic(FirebaseLoadTasksEventListener firebaseLoadTasksEventListener) {
        tasksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshotToTask(dataSnapshot);
                    if(task != null) {
                        if (firebaseLoadTasksEventListener != null) {
                            firebaseLoadTasksEventListener.onTaskAdded(task, prevChildKey);
                        }
                    } else {
                        firebaseLoadTasksEventListener.onError("Unknown error.");
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshotToTask(dataSnapshot);
                    if(task != null) {
                        if (firebaseLoadTasksEventListener != null) {
                            firebaseLoadTasksEventListener.onTaskChanged(task, prevChildKey);
                        }
                    } else {
                        firebaseLoadTasksEventListener.onError("Unknown error.");
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshotToTask(dataSnapshot);
                    if(task != null) {
                        if (firebaseLoadTasksEventListener != null) {
                            firebaseLoadTasksEventListener.onTaskRemoved(task);
                        }
                    } else {
                        firebaseLoadTasksEventListener.onError("Unknown error.");
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshotToTask(dataSnapshot);
                    if(task != null) {
                        if (firebaseLoadTasksEventListener != null) {
                            firebaseLoadTasksEventListener.onTaskMoved(task, prevChildKey);
                        }
                    } else {
                        firebaseLoadTasksEventListener.onError("Unknown error.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (firebaseLoadTasksEventListener != null) {
                    firebaseLoadTasksEventListener.onError(databaseError.getMessage());
                }
            }
        });
    }

    private Task dataSnapshotToTask(DataSnapshot dataSnapshot) {
        Task task = dataSnapshot.getValue(Task.class);
        if(task != null) {
            task.setUid(dataSnapshot.getKey());
            return task;
        } else {
            return null;
        }
    }

    public String generateTaskId() {
        return tasksReference.push().getKey();
    }

    public void addTask(final Task task, final FirebaseChangeTaskListener firebaseChangeTaskListener) {
        tasksReference.child(task.getUid()).setValue(task).addOnCompleteListener(task1 -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onComplete();
            }
        }).addOnFailureListener(e -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onError(e.getMessage());
            }
        });
    }

    public void updateTask(final Task task, final FirebaseChangeTaskListener firebaseChangeTaskListener) {
        tasksReference.child(task.getUid()).setValue(task).addOnCompleteListener(task1 -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onComplete();
            }
        }).addOnFailureListener(e -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onError(e.getMessage());
            }
        });
    }

    public void removeTask(final Task task, final FirebaseChangeTaskListener firebaseChangeTaskListener) {
        tasksReference.child(task.getUid()).removeValue().addOnCompleteListener(task1 -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onComplete();
            }
        }).addOnFailureListener(e -> {
            if(firebaseChangeTaskListener != null) {
                firebaseChangeTaskListener.onError(e.getMessage());
            }
        });
    }
}
