package pl.patryk_lawicki.tasklist.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.patryk_lawicki.tasklist.R;
import pl.patryk_lawicki.tasklist.models.User;

public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;

    private ProgressBar progressBar;
    private TextView lgonInText;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        /* UI elements */
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lgonInText = (TextView) findViewById(R.id.loginIn);
        signInButton = (SignInButton) findViewById(R.id.loginAsGoogle);


        /* Google SignIn define */
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_LONG).show())
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        /* check is logged */
        if (firebaseUser != null) {
            runMainActivity();
        } else {
            firebaseAuth.signOut();

            if (googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        status -> Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show());
            }
        }

        /* Add action to button sign in */
        signInButton.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            lgonInText.setVisibility(TextView.VISIBLE);
            signInButton.setVisibility(SignInButton.INVISIBLE);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    /* Handle result from google auth */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount account = googleSignInResult.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG, googleSignInResult.getStatus().toString());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                lgonInText.setVisibility(TextView.INVISIBLE);
                signInButton.setVisibility(SignInButton.VISIBLE);
                Toast.makeText(this, googleSignInResult.getStatus().getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /* Get sign in status in firebase */
    protected void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                addToDatabaseUser();
            }

            if (!task.isSuccessful()) {
                Log.w(TAG, "signInWithCredential", task.getException());
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Add user data to firebase database */
    protected void addToDatabaseUser() {
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;

        userReference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            lgonInText.setVisibility(TextView.INVISIBLE);
                            runMainActivity();
                        } else {
                            User user = new User();
                            user.setUid(firebaseUser.getUid());
                            user.setName(firebaseUser.getDisplayName());
                            user.setEmail(firebaseUser.getEmail());

                            userReference.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                lgonInText.setVisibility(TextView.INVISIBLE);
                                runMainActivity();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Add user to database failed.", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        lgonInText.setVisibility(TextView.INVISIBLE);
                        signInButton.setVisibility(SignInButton.VISIBLE);
                    }
                });

    }

    private void runMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
