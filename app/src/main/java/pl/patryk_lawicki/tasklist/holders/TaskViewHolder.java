package pl.patryk_lawicki.tasklist.holders;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.patryk_lawicki.tasklist.R;
import pl.patryk_lawicki.tasklist.models.Task;

/**
 * Created by patry on 09.05.2017.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private DatabaseReference tasksReference = FirebaseDatabase.getInstance().getReference("Tasks").child(firebaseUser.getUid());

    private CheckBox complete;
    private TextView name;
    private ImageButton delete;

    public TaskViewHolder(View itemView) {
        super(itemView);
        complete = (CheckBox) itemView.findViewById(R.id.taskComplete);
        name = (TextView) itemView.findViewById(R.id.taskName);
        delete = (ImageButton) itemView.findViewById(R.id.taskDelete);
    }

    public void bind(final Task task) {
        complete.setChecked(task.isComplete());

        complete.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox)v;
            task.setComplete(checkBox.isChecked());
            setCompleteName(checkBox.isChecked());
            tasksReference.child(task.getUid()).setValue(task).addOnFailureListener(e -> {
                complete.setChecked(!checkBox.isChecked());
                setCompleteName(complete.isChecked());
            });
        });

        name.setText(task.getName());
        setCompleteName(task.isComplete());
        delete.setOnClickListener(v -> tasksReference.child(task.getUid()).removeValue());
    }

    private void setCompleteName(boolean complete) {
        if (complete) {
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
