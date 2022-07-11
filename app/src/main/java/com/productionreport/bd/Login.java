package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.OrphanUtilityMethods;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class Login extends AppCompatActivity {

    FrameLayout parentLayout;
    LinearLayout mainLayout;
    LinearLayout progressLayout;
    TextView errorMessageTextView, signUpTextView, forgotPasswordTextView;
    EditText emailEditText, passwordEditText;
    Button signIn;
    FirebaseAuth mAuth;
    SignInButtonController signInButtonController = new SignInButtonController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLayout = findViewById(R.id.parent_layout);
        mainLayout = findViewById(R.id.main_layout);
        progressLayout = findViewById(R.id.progress_layout);
        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.INVISIBLE);

        forgotPasswordTextView = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.sign_up_textview);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signIn = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(Login.this);
                signIn.setEnabled(false);
                mainLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, PasswordRecovery.class));
            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(Login.this);
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    signInButtonController.isEmailNotEmpty = false;
                    signIn.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }else{
                    signInButtonController.isEmailNotEmpty = true;
                    signIn.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    signInButtonController.isPasswordNotEmpty = false;
                    signIn.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }else{
                    signInButtonController.isPasswordNotEmpty = true;
                    signIn.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            userNotNull(user);
        }
    }

    private void signInWithEmailAndPassword(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser user = authResult.getUser();
                        if(user != null){
                            userNotNull(user);
                        }else{
                            // maybe this never happens
                            Log.i("sign_in", "User is null");
                            progressLayout.setVisibility(View.INVISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            signIn.setEnabled(true);
                            errorMessageTextView.setText("User not found");
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("sign_in", "failed");
                        progressLayout.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                        signIn.setEnabled(true);
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Log.i("error", "FirebaseAuthInvalidUserException");
                            errorMessageTextView.setText("The email is disabled or doesn't exist!");
                        }
                        else if(e instanceof FirebaseAuthInvalidCredentialsException){
                            Log.i("error", "FirebaseAuthInvalidCredentialsException");
                            errorMessageTextView.setText("Email or password is wrong!");
                        }
                        else{
                            errorMessageTextView.setText("Couldn't sign in!");
                        }
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        passwordEditText.setText("");
                    }
                });
    }

    private class SignInButtonController{
        boolean isEmailNotEmpty = false;
        boolean isPasswordNotEmpty = false;

        boolean shouldButtonBeEnabled(){
            return isEmailNotEmpty && isPasswordNotEmpty;
        }
    }

    private void userNotNull(FirebaseUser user){
        if(user.isEmailVerified()){
            Intent intent = new Intent(Login.this, HomePageSelector.class);
            startActivity(intent);
            Login.this.finish();
        }else{
            user.sendEmailVerification()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(Login.this, EmailVerification.class);
                            startActivity(intent);
                            Login.this.finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressLayout.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(Login.this, "Couldn't send verification email!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
