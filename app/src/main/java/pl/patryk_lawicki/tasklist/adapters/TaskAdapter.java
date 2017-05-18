package pl.patryk_lawicki.tasklist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.patryk_lawicki.tasklist.R;
import pl.patryk_lawicki.tasklist.holders.TaskViewHolder;
import pl.patryk_lawicki.tasklist.models.Task;

/**
 * Created by patry on 09.05.2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private static final String TAG = TaskAdapter.class.getSimpleName();


    private List<Task> taskList = new ArrayList<>();

    public TaskAdapter() {
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_card_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bind(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addTask(Task task) {
        taskList.add(task);
        Collections.sort(taskList, Collections.reverseOrder(new TaskComparator()));
        notifyDataSetChanged();
    }

    public void updateTask(Task task) {
        taskList.set(taskList.indexOf(findTask(task.getUid())), task);
        notifyDataSetChanged();
    }

    public void removeTask(Task task) {
        taskList.remove(findTask(task.getUid()));
        Collections.sort(taskList, Collections.reverseOrder(new TaskComparator()));
        notifyDataSetChanged();
    }

    private Task findTask(String uId) {
        Task taskOld = null;
        for (Task t : taskList) {
            if (t.getUid().equals(uId)) {
                taskOld = t;
                break;
            }
        }
        assert taskOld != null;

        return taskOld;
    }

    private class TaskComparator implements Comparator<Task> {

        @Override
        public int compare(Task task, Task task2) {
            return new DateTime(task.getCreated()).compareTo(new DateTime(task2.getCreated()));
        }
    }
}
