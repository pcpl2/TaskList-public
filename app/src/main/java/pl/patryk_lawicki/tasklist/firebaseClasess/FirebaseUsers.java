package pl.patryk_lawicki.tasklist.firebaseClasess;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.patryk_lawicki.tasklist.listeners.FirebaseAddUserListener;
import pl.patryk_lawicki.tasklist.listeners.FirebaseLoadUserListener;
import pl.patryk_lawicki.tasklist.models.User;

/**
 * Created by patry on 13.07.2017.
 */

public class FirebaseUsers {
    private static final String TAG = "FirebaseUsersSupport";

    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");

    private static FirebaseUsers instance = new FirebaseUsers();
    public static FirebaseUsers getInstance() {
        return instance;
    }

    public void loadUserStatic(final String userId, final FirebaseLoadUserListener firebaseLoadUserListener) {
        if (userId == null) {
            Log.e(TAG, "User id is null");
            if (firebaseLoadUserListener != null) {
                firebaseLoadUserListener.onError("User id is null");
            }
        } else {
            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setUid(dataSnapshot.getKey());

                        if (firebaseLoadUserListener != null) {
                            firebaseLoadUserListener.onCompleteExist(user);
                        }
                    } else {
                        if (firebaseLoadUserListener != null) {
                            firebaseLoadUserListener.OnCompleteNotExist();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                    if (firebaseLoadUserListener != null) {
                        firebaseLoadUserListener.onError(databaseError.getMessage());
                    }
                }
            });
        }
    }

    public void addUser(final User user, final FirebaseAddUserListener firebaseAddUserListener) {
        usersReference.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
            if(firebaseAddUserListener != null) {
                firebaseAddUserListener.onComplete();
            }
        }).addOnFailureListener(e -> {
            if(firebaseAddUserListener != null) {
                firebaseAddUserListener.onError(e.getMessage());
            }
        });

    }
}
