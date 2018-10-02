package hr.tvz.trackmydog.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.firebase.FBAuth;
import hr.tvz.trackmydog.R;

/**
 * A login screen that offers login via google.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Login Activity";

    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;

    @BindView(R.id.status) TextView mStatusTextView;
    @BindView(R.id.login_layout) LinearLayout loginLayout;
    @BindView(R.id.loading_layout) LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Log.d(TAG, "on create");

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // TODO - configure sign in (if needed - error) ???
        // TODO - init firebase auth
        configureSignIn();
        FBAuth.initializeFirebaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "on start - check if user is logged in");

        FirebaseUser currentUser = FBAuth.mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "user logged in - get info and show main activity");
            FBAuth.getFirebaseUser(currentUser, this);
        } else {
            Log.d(TAG, "user isn't logged - show google sign-in option");
            loginLayout.setVisibility(View.VISIBLE);
            // loading layout (dog image):
            loadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result ***");

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
                // TODO - show an error message:
                mStatusTextView.setText("Google sign in failed");
                mStatusTextView.setTextColor(getResources().getColor(R.color.colorAccent, null));
                // updateUI(null);
            }
        }
    }


    /**
     * TODO - check what should be here (except user log in)
     * Takes google account and get user info.
     * Start auth with google.
     * @param acct = Google Account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // progress dialog:
        showProgressDialog();

        // TODO - additional (cause 'this' is from firebase):
        final Context context = this;

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FBAuth.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential: success");
                        FirebaseUser user = FBAuth.mAuth.getCurrentUser();
                        FBAuth.getFirebaseUser(user, context);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential: failure", task.getException());
                        // TODO - snackbar deleted:
                        hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                    }
                });
    }


    // initialize google sign in at the begining:
    public void configureSignIn() {
        System.out.println("*** configure sign in (google)");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // TODO - what is this ID:
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void signIn() {
        System.out.println("*** sign in (google) - open intent (start RC activity)");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }
}

