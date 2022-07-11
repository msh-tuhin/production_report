package com.productionreport.bd;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import myapp.utils.OrphanUtilityMethods;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerification extends AppCompatActivity {

    private String mEmail;
    FrameLayout parentLayout;
    LinearLayout mainLayout;
    LinearLayout progressLayout;
    TextView resendEmailTextView, emailNotVerifiedTextView;
    EditText emailEditText, passwordEditText;
    Button signInButton;
    FirebaseAuth mAuth;
    private final String TAG = "EMAILVERIFICATION_";
    SignInButtonController signInButtonController = new SignInButtonController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // maybe name, email not needed from intent
        // could be acquired from the FirebaseUser object
        mEmail = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();

        parentLayout = findViewById(R.id.parent_layout);
        mainLayout = findViewById(R.id.main_layout);
        progressLayout = findViewById(R.id.progress_layout);
        emailNotVerifiedTextView = findViewById(R.id.not_verified_text);
        emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
        resendEmailTextView = findViewById(R.id.resend_email_textview);

        emailEditText = findViewById(R.id.email);
        emailEditText.setText(mEmail);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);

        mAuth.signOut();

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    signInButtonController.isEmailNotEmpty = false;
                    signInButton.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }else{
                    signInButtonController.isEmailNotEmpty = true;
                    signInButton.setEnabled(signInButtonController.shouldButtonBeEnabled());
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
                if(s.toString().equals("")){
                    signInButtonController.isPasswordNotEmpty = false;
                    signInButton.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }else{
                    signInButtonController.isPasswordNotEmpty = true;
                    signInButton.setEnabled(signInButtonController.shouldButtonBeEnabled());
                }
            }
        });

        resendEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(EmailVerification.this);
                signInButton.setEnabled(false);
                mainLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInWithEmailAndPassword(email, password);
            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(EmailVerification.this);
            }
        });
    }

    @Override
    protected void onStart() {
        //OrphanUtilityMethods.checkUpdateMust(this);
        //OrphanUtilityMethods.checkMaintenanceBreak(this);
        super.onStart();
    }

    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if(user != null){
                            if(user.isEmailVerified()){
                                Log.i("sign_in", "Successful");
                                Intent intent = new Intent(EmailVerification.this, CreateJoinFactory.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Log.i("sign_in", "Email not verified");
                                progressLayout.setVisibility(View.INVISIBLE);
                                mainLayout.setVisibility(View.VISIBLE);
                                signInButton.setEnabled(true);
                                // showDialog();
                                emailNotVerifiedTextView.setText("Sorry! Email is not verified!");
                                emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                                passwordEditText.setText("");
                            }
                        }else{
                            // maybe this never happens
                            Log.i("sign_in", "User is null");
                            progressLayout.setVisibility(View.INVISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            signInButton.setEnabled(true);
                            emailNotVerifiedTextView.setText("User not found");
                            emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("sign_in", "failed");
                        progressLayout.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                        signInButton.setEnabled(true);
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Log.i("error", "FirebaseAuthInvalidUserException");
                            emailNotVerifiedTextView.setText("The email is disabled or doesn't exist!");
                        }
                        else if(e instanceof FirebaseAuthInvalidCredentialsException){
                            Log.i("error", "FirebaseAuthInvalidCredentialsException");
                            emailNotVerifiedTextView.setText("Email or password is wrong!");
                        }
                        else{
                            emailNotVerifiedTextView.setText("Couldn't sign in!");
                        }
                        emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                        passwordEditText.setText("");
                    }
                });
    }

    private void resendVerificationEmail(){
        emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i(TAG+"ver_e", "sent");
                        String message = "Verification Email sent to " + user.getEmail();
                        Toast.makeText(EmailVerification.this, message, Toast.LENGTH_LONG).show();
                    }else{
                        // TODO show a dialog with the error message
                        // TODO there are some quota limitations
                        // TODO handle those
                        // TODO add a firestore collection for saving
                        // TODO verification email not sent error messages
                        // TODO so that they can be checked by an admin later
                        Log.i(TAG+"error", task.getException().getMessage());
                        Toast.makeText(EmailVerification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Log.i("user", "null");
        }
    }

    private class SignInButtonController{
        boolean isEmailNotEmpty = true;
        boolean isPasswordNotEmpty = false;

        boolean shouldButtonBeEnabled(){
            return isEmailNotEmpty && isPasswordNotEmpty;
        }
    }

}
