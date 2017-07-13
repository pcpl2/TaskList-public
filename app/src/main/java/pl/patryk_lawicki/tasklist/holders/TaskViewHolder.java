package pl.patryk_lawicki.tasklist.holders;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import pl.patryk_lawicki.tasklist.R;
import pl.patryk_lawicki.tasklist.firebaseClasess.FirebaseTasks;
import pl.patryk_lawicki.tasklist.listeners.FirebaseChangeTaskListener;
import pl.patryk_lawicki.tasklist.models.Task;

/**
 * Created by patry on 09.05.2017.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder {

    private FirebaseTasks firebaseTasks = FirebaseTasks.getInstance();

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
            CheckBox checkBox = (CheckBox) v;
            task.setComplete(checkBox.isChecked());
            setCompleteName(checkBox.isChecked());
            firebaseTasks.updateTask(task, new FirebaseChangeTaskListener() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onError(String message) {
                    complete.setChecked(!checkBox.isChecked());
                    setCompleteName(complete.isChecked());
                }
            });
        });

        name.setText(task.getName());
        setCompleteName(task.isComplete());
        delete.setOnClickListener(v -> firebaseTasks.updateTask(task, new FirebaseChangeTaskListener() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(String message) {
            }
        }));
    }

    private void setCompleteName(boolean complete) {
        if (complete) {
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
