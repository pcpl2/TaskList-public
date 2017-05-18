package pl.patryk_lawicki.tasklist.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import io.reactivex.Observable;

import pl.patryk_lawicki.tasklist.R;
import pl.patryk_lawicki.tasklist.adapters.TaskAdapter;
import pl.patryk_lawicki.tasklist.models.Task;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private DatabaseReference tasksReference = FirebaseDatabase.getInstance().getReference("Tasks").child(firebaseUser.getUid());

    private TaskAdapter taskAdapter;

    private EditText taskNameInput;
    private Button addTaskButton;
    private RecyclerView taskListView;
    private ProgressBar progressBar;
    private TextView noTasks;

    private boolean listVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* UI elements */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.ToolBarMain);
        setSupportActionBar(myToolbar);

        taskNameInput = (EditText) findViewById(R.id.taskTextInput);
        addTaskButton = (Button) findViewById(R.id.addButton);
        taskListView = (RecyclerView) findViewById(R.id.taskViewList);
        progressBar = (ProgressBar) findViewById(R.id.loadingTasks);
        noTasks = (TextView) findViewById(R.id.noTaskTextView);

        /* Default visibility */
        noProgressBarVisibility();

        /* Dynamic activate add button after add any text in input */
        Observable<String> taskInputTextObservable = Observable.create(emitter -> {
            final TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    emitter.onNext(s.toString());

                    addTaskButton.setEnabled(s.length() > 0);
                }
            };
            taskNameInput.addTextChangedListener(watcher);

            emitter.setCancellable(() -> taskNameInput.removeTextChangedListener(watcher));
        });

        taskInputTextObservable.subscribe();

        /* Initialize task adapter */
        taskAdapter = new TaskAdapter();

        /* List settings */
        taskListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskListView.setAdapter(taskAdapter);

        /* Run getting data from database */
        getTasksFromFirebase();

        /* Handle add task button */
        addTaskButton.setOnClickListener(v -> {
            taskNameInput.setEnabled(false);
            addTaskButton.setEnabled(false);
            String taskId = tasksReference.push().getKey();
            Task task = new Task(false, taskNameInput.getText().toString(), DateTime.now().getMillis());
            tasksReference.child(taskId).setValue(task).addOnCompleteListener(task1 -> {
                taskNameInput.setText("");
                taskNameInput.setEnabled(true);
            }).addOnFailureListener(e -> {
                taskNameInput.setEnabled(true);
                addTaskButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }

    /**
     * Function to get and update data in list as firebase database.
     */
    private void getTasksFromFirebase() {
        tasksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    noTaskVisibility();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                noTaskVisibility();
            }
        });

        tasksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    task.setUid(dataSnapshot.getKey());
                    taskAdapter.addTask(task);
                }
                if ((!listVisibility) && (taskAdapter.getItemCount() > 0)) {
                    listVisibility();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    task.setUid(dataSnapshot.getKey());
                    taskAdapter.updateTask(task);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    task.setUid(dataSnapshot.getKey());
                    taskAdapter.removeTask(task);
                }

                if ((listVisibility) && (taskAdapter.getItemCount() == 0)) {
                    noTaskVisibility();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                noTaskVisibility();
            }
        });
    }

    /**
     * Support function to set list visibility
     */
    private void listVisibility() {
        listVisibility = true;
        taskListView.setVisibility(RecyclerView.VISIBLE);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        noTasks.setVisibility(TextView.INVISIBLE);
    }

    /**
     * Support function to set text visibility
     */
    private void noTaskVisibility() {
        listVisibility = false;
        taskListView.setVisibility(RecyclerView.INVISIBLE);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        noTasks.setVisibility(TextView.VISIBLE);
    }

    /**
     * Support function to set progressbar visibility
     */
    private void noProgressBarVisibility() {
        listVisibility = false;
        taskListView.setVisibility(RecyclerView.INVISIBLE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        noTasks.setVisibility(TextView.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
