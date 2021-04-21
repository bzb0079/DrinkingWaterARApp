package app.th.project.drinkingWaterAR.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.th.project.drinkingWaterAR.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private TextView emailText;
    private TextView passwordText;
    private ProgressBar progressBar;
    private Button loginButton;
    private TextView newAccount;
    private TextView emailError, passwordError;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Intent intent;
    private FirebaseUser currentUser;

    String emptyEmailError = "Enter a valid email address.";
    String validEmailError = "Email address is not valid.";
    String emptyPasswordError = "Create a password.";
    String validPasswordError = "At least 6 characters are required for password.";

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
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkUserAccessLevel(currentUser.getUid());
        } else {
            setContentView(R.layout.activity_sign_in);
            initView();
        }
    }


    private void initView() {
        emailText = (TextView) findViewById(R.id.email);
        passwordText = (TextView) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        newAccount = (TextView) findViewById(R.id.registerLink);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        passwordError = (TextView) findViewById(R.id.passwordInputError);
        emailError = (TextView) findViewById(R.id.emailInputError);
        emailError.setText(null);
        passwordError.setText(null);
        //click on register if new user
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
//        // enter email and password and click login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEmail(emailText.getText().toString())
                        && validatePassword(passwordText.getText().toString())) {
                    startSignIn();
                }
            }
        });
    }

    private void startSignIn() {
//        progressBar.setVisibility(View.VISIBLE);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUserAccessLevel(authResult.getUser().getUid());
                    }
                }).addOnFailureListener(e -> Toast.makeText(SignInActivity.this, "password or the username is incorrect", Toast.LENGTH_LONG).show());
    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = db.collection("Users").document(uid);
        // extract data from document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("Tag", "onSucess" + documentSnapshot.getData());
                if(documentSnapshot.get("isUser") != null){
                    // user is not admin
                    intent = new Intent(SignInActivity.this, AddComplaintsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // user is admin
                    intent = new Intent(SignInActivity.this, ViewComplaintActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }

}