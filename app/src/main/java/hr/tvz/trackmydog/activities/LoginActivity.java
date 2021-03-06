package hr.tvz.trackmydog.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.tvz.trackmydog.BaseActivity;
import hr.tvz.trackmydog.MainActivity;
import hr.tvz.trackmydog.MyApplication;
import hr.tvz.trackmydog.firebaseModel.CurrentUserViewModel;
import hr.tvz.trackmydog.firebaseServices.AuthService;
import hr.tvz.trackmydog.firebaseServices.FBAuthService;
import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.models.forms.NewUserForm;
import hr.tvz.trackmydog.callbacks.StartIntentCallback;
import hr.tvz.trackmydog.firebaseServices.TokenService;

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

        // init firebase auth
        configureSignIn();
        FBAuthService.initializeFirebaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "on start - check if user is logged in");

        // check if the user is signed in
        if (FBAuthService.isUserLoggedIn()) {
            Log.d(TAG, "user logged in - get info and show main activity");
            loginUser();
        } else {
            Log.d(TAG, "user isn't logged - show google sign-in option");
            loginLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void loginUser() {
        setUserListener(context -> {
            Log.d(TAG, "user listeners set = start MainActivity");
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            // back button closes the app (doesnt return to login)
            ((Activity) context).finish();
        });
    }

    private void setUserListener(final StartIntentCallback callback) {
        MyApplication.setUserKey(FBAuthService.getUserKey());
        Log.d(TAG, "set user listener: " + FBAuthService.getUserEmail()
                + " - key: " + MyApplication.getUserKey());
        final String email = FBAuthService.getUserEmail();
        final String token = TokenService.getAppToken();
        final Context context = this;

        // get the user data form listener
        new ViewModelProvider(this).get(CurrentUserViewModel.class)
                .getCurrentUserLiveData().observe(this, currentUser -> {
                    Log.d(TAG, "current user: " + currentUser);
                    if (currentUser != null) {
                        // check if token is the same
                        // if null or not equal, save new one to FB:
                        if (currentUser.getToken() == null || !currentUser.getToken().equals(token)) {
                            Log.e(TAG, "token is null or not valid - save new one on FB");
                            AuthService.changeToken(currentUser, token);
                        }
                    } else {
                        // register new user:
                        NewUserForm user = new NewUserForm(email, token);
                        AuthService.addUser(user);
                    }

                    // continue with the app:
                    callback.startIntent(context);
                });
    }


    // initialize google sign in at the begining:
    public void configureSignIn() {
        Log.d(TAG, "configure sign in (google)");
        // Configure Google Sign In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {// opening sign in window with possible users
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }

    /**
     * Shows Google dialog, to choose the account. Opened on button click.
     */
    private void signIn() {
        Log.d(TAG, "*** sign in (google) - open intent (start RC activity)");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on Google dialog result");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null : "Google Account is null";
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, show message
                Log.w(TAG, "Google sign in failed", e);
                mStatusTextView.setText("Google sign in failed");
            }
        }
    }

    /**
     * Takes google account and get user info.
     * Start auth with google.
     * @param acct = Google Account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // progress dialog:
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FBAuthService.mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential: success");
                    loginUser();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential: failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            });
    }
}