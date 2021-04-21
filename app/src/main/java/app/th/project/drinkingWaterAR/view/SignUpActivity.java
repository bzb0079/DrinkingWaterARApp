package app.th.project.drinkingWaterAR.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import app.th.project.drinkingWaterAR.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private ScrollView root;
    private TextView emailText;
    private TextView passwordText;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView emailError, passwordError, firstNameError, lastNameError, confirmpasswordError;
    private ProgressBar progressBar;
    private Button registerButton;
    private TextView loginLink;
    private Intent intent;
    private String userID;
    private FirebaseAuth mAuth;
    String emptyFirstNameError = "Enter your first name.";
    String emptyLastNameError = "Enter your last name.";
    String validFirstNameError = "First Name is not valid.";
    String validLastNameError = "Last Name is not valid.";
    String emptyEmailError = "Enter a valid email address.";
    String validEmailError = "Email address is not valid.";
    String emptyPasswordError = "Create a password.";
    String validPasswordError = "At least 6 characters are required for password.";
    String validConfirmPasswordError = "Password fields do not match.";

    // validation regex
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L} .'-]+$");
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // [START FORM VALIDATION]
    private boolean validateFirstName(String text) {
        if (text.isEmpty()) {
            firstNameError.setText(emptyFirstNameError);
            return false;
        } else if (!NAME_PATTERN.matcher(text).matches()) {
            firstNameError.setText(validFirstNameError);
            return false;
        } else {
            firstNameError.setText(null);
            return true;
        }
    }

    private boolean validateLastName(String text) {
        if (text.isEmpty()) {
            lastNameError.setText(emptyLastNameError);
            return false;
        } else if (!NAME_PATTERN.matcher(text).matches()) {
            lastNameError.setText(validLastNameError);
            return false;
        } else {
            lastNameError.setText(null);
            return true;
        }
    }

    private boolean validateEmail(String text) {
        if (text.isEmpty()) {
            emailError.setText(emptyEmailError);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            emailError.setText(validEmailError);
            return false;
        } else {
            emailError.setText(null);
            return true;
        }
    }

    private boolean validatePassword(String text) {
        if (text.isEmpty()) {
            passwordError.setText(emptyPasswordError);
            return false;
        } else if(text.length() < 6) {
            passwordError.setText(validPasswordError);
            return false;
        } else {
            passwordError.setText(null);
            return true;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
         initView();
    }
    private void initView() {
        root = (ScrollView) findViewById(R.id.root);
        emailText = (TextView) findViewById(R.id.email);
        firstNameText = (TextView) findViewById(R.id.firstname);
        lastNameText = (TextView) findViewById(R.id.lastname);
        passwordText = (TextView) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginLink = (TextView) findViewById(R.id.loginLink);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        firstNameError = (TextView) findViewById(R.id.firstnameInputError);
        lastNameError = (TextView) findViewById(R.id.lastnameInputError);
        passwordError = (TextView) findViewById(R.id.passwordInputError);
        emailError = (TextView) findViewById(R.id.emailInputError);
        emailError.setText(null);
        firstNameError.setText(null);
        lastNameError.setText(null);
        passwordError.setText(null);


        // click on login if existing user
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        // enter details and click register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFirstName(firstNameText.getText().toString()) &
                        validateLastName(lastNameText.getText().toString())
                        & validateEmail(emailText.getText().toString())
                        & validatePassword(passwordText.getText().toString())) {
                    startSignUp();
                }
            }
        });
    }

    // start registration process
    private void startSignUp() {
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String firstName = firstNameText.getText().toString();
        final String lastName = lastNameText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, add details in database
//                            User user;
//                            user = new User(name, email);
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Successfully registered, please sign in now",Toast.LENGTH_LONG).show();
                            DocumentReference df = db.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FirstName", firstName);
                            userInfo.put("LastName", lastName);
                            userInfo.put("Email", email);
                            userInfo.put("isUser", "1");
                            df.set(userInfo);

                            intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // if something went wrong, show Error
                            Toast.makeText(SignUpActivity.this, "Something is wrong. Please try again later.!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Snackbar snackbar = Snackbar
                                    .make(root, "The e-mail ID you entered is already in use. Use another e-mail ID to create a new account.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(root, "Error while connecting the server. Please try again later.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}