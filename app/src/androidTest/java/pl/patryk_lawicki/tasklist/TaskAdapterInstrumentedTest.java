package pl.patryk_lawicki.tasklist;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.patryk_lawicki.tasklist.adapters.TaskAdapter;
import pl.patryk_lawicki.tasklist.models.Task;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TaskAdapterInstrumentedTest {
    @Test
    public void taskAdapterCountTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        RecyclerView recyclerView = new RecyclerView(appContext);
        TaskAdapter taskAdapter = new TaskAdapter();
        Task task1 = new Task(false, "test1", DateTime.now().getMillis());

        recyclerView.setAdapter(taskAdapter);

        task1.setUid("uuid1");

        taskAdapter.addTask(task1);

        assertEquals(1, taskAdapter.getItemCount());
    }

    @Test
    public void taskAdapterUpdateRemoveTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        RecyclerView recyclerView = new RecyclerView(appContext);
        TaskAdapter taskAdapter = new TaskAdapter();
        Task task1 = new Task(false, "test1", DateTime.now().getMillis());
        Task task2 = new Task(true, "Its ok", DateTime.now().getMillis());

        recyclerView.setAdapter(taskAdapter);

        task1.setUid("uuid1");
        task2.setUid("uuid2");

        taskAdapter.addTask(task1);
        taskAdapter.addTask(task2);

        task1.setComplete(true);
        task1.setName("Task update test");

        taskAdapter.updateTask(task1);

        assertEquals(2, taskAdapter.getItemCount());

        taskAdapter.removeTask(task1);

        assertEquals(1, taskAdapter.getItemCount());

    }
}
