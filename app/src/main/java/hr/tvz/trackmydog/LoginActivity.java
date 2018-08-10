package hr.tvz.trackmydog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via google.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;

    @BindView(R.id.status) TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        ButterKnife.bind(this);

        System.out.println("*** Login = on create (1)");

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // TODO - configure sign in (if needed - error) ???
        // TODO - init firebase auth
        configureSignIn();
        FBAuth.initializeFirebaseAuth();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

        System.out.println("*** Login = on start (2)");

        // TODO - check if user is logged in (and check with the local user also):
        // Check if user is signed in (non-null) and update UI accordingly.

        // TODO - check if FB logged in, and add local user
        // TODO - if not, then show login page to register user
        System.out.println("Login activity - on start - check if user is logged in");
        FBAuth.checkIfUserIsLoggedIn(this);
    }



    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("*** Login = on activity result (3)");

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]


    /**
     * TODO - check what should be here (except user log in)
     * Takes google account and get user info
     * @param acct = Google Account
     */
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        // TODO - additional (cause 'this' is from firebase):
        final Context context = this;

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FBAuth.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = FBAuth.mAuth.getCurrentUser();
                        FBAuth.loginUser(user, context);
                        // updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        // TODO - snackbar deleted:
                        Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        // updateUI(null);
                    }
                    // [START_EXCLUDE]
                    hideProgressDialog();
                    // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    // initialize google sign in at the begining:
    public void configureSignIn() {
        System.out.println("*** configure sign in (google)");
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // TODO - what is this ID:
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    // [START signin]
    private void signIn() {
        System.out.println("*** sign in (google) - open intent (start RC activity)");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }
}

