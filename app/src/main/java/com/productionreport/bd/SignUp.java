package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.InputValidator;
import myapp.utils.OrphanUtilityMethods;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.productionreport.bd.R;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    FrameLayout parentLayout;
    LinearLayout formLayout;
    LinearLayout progressLayout;
    TextInputLayout nameLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextInputLayout passwordConfirmLayout;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    TextInputEditText passwordConfirmEditText;
    TextInputEditText nameEditText;
    CheckBox agreeToPolicyCheckbox;
    TextView askPolicyTV;
    Button signUp;
    FirebaseAuth mAuth;

    SignUpButtonController signUpButtonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        parentLayout = findViewById(R.id.parent_layout);
        formLayout = findViewById(R.id.form_layout);
        progressLayout = findViewById(R.id.progress_layout);
        nameLayout = findViewById(R.id.input_layout_name);
        emailLayout = findViewById(R.id.input_layout_email);
        passwordLayout = findViewById(R.id.input_layout_password);
        passwordConfirmLayout = findViewById(R.id.input_layout_password_confirm);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        passwordConfirmEditText = findViewById(R.id.password_confirm);
        // agreeToPolicyCheckbox = findViewById(R.id.agree_to_policy);
        // askPolicyTV = findViewById(R.id.policy_tv);
        signUp = findViewById(R.id.sign_up_button);

        signUpButtonController = new SignUpButtonController(signUp);

        nameEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        emailEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        passwordEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        passwordConfirmEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        addTextChangedListeners();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(SignUp.this);
                signUp.setEnabled(false);
                formLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                String nameText = nameEditText.getText().toString();
                String emailText = emailEditText.getText().toString();
                String passwordText = passwordEditText.getText().toString();
                createUserWithEmailAndPassword(emailText, passwordText, nameText);
            }
        });

//        askPolicyTV.setText(getAskPolicyText());
//        askPolicyTV.setMovementMethod(LinkMovementMethod.getInstance());
//        //askPolicyTV.setHighlightColor(Color.TRANSPARENT);
//        agreeToPolicyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                signUpButtonController.isPolicyChecked = isChecked;
//                signUpButtonController.enableOrDisableSignUpButton();
//            }
//        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrphanUtilityMethods.hideKeyboard(SignUp.this);
            }
        });
    }

    @Override
    protected void onStart() {
        //OrphanUtilityMethods.checkUpdateMust(this);
        //OrphanUtilityMethods.checkMaintenanceBreak(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void createUserWithEmailAndPassword(final String email, final String password, final String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();

                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        user.updateProfile(userProfileChangeRequest)
                                .addOnSuccessListener(SignUp.this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("name", "added");
                                    }
                                })
                                .addOnFailureListener(SignUp.this, new OnFailureListener() {
                                    @Override
                                    // the chances for this failure are very thin
                                    public void onFailure(@NonNull Exception e) {
                                        if(e instanceof FirebaseAuthInvalidUserException){
                                            Log.i("error", e.getMessage());
                                        }
                                    }
                                });

                        Map<String, String> pData = new HashMap<>();
                        pData.put(FirestoreFieldNames.PERSON_NAME, name);
                        pData.put(FirestoreFieldNames.PERSON_EMAIL, email);
                        FirebaseFirestore.getInstance().collection("person_vital")
                                .document(user.getUid()).set(pData);
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(SignUp.this, EmailVerification.class);
                                        intent.putExtra("email", email);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        // finish();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressLayout.setVisibility(View.INVISIBLE);
                        formLayout.setVisibility(View.VISIBLE);
                        signUp.setEnabled(true);
                        Exception exception = e;
                        if(exception instanceof FirebaseAuthWeakPasswordException){
//                            passwordEditText.setText("");
//                            passwordConfirmEditText.setText("");
                            passwordLayout.setError(((FirebaseAuthWeakPasswordException) exception).getReason());
                        } else{
                            // FirebaseAuthInvalidCredentialsException
                            // FirebaseAuthUserCollisionException
                            emailLayout.setError(exception.getMessage());
                        }
                    }
                });
    }

    private void addTextChangedListeners(){
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkNameEditText(s.toString());
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
                checkEmailEditText(s.toString());
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
                checkPasswordEditText(s.toString());
            }
        });

        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordConfirmEditText(passwordEditText.getText().toString(), s.toString());
            }
        });
    }

    private void checkNameEditText(String name){
//        if(name.trim().isEmpty()){
//            nameLayout.setError(null);
//            nameLayout.setError("Name is required");
//            return;
//        }
        boolean validated = InputValidator.validateName(name);
        nameLayout.setError(null);
        if(!validated){
            nameLayout.setError("Name is required");
        }
        signUpButtonController.isNameValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkEmailEditText(String email){
//        if(email.trim().isEmpty()){
//            emailLayout.setError(null);
//            emailLayout.setError("Email is required");
//            return;
//        }
        boolean validated = InputValidator.validateEmail(email);
        emailLayout.setError(null);
        if(!validated){
            emailLayout.setError("Please type a valid email");
        }
        signUpButtonController.isEmailValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkPasswordEditText(String password){
//        if(password.trim().isEmpty()){
//            passwordLayout.setError(null);
//            passwordLayout.setError("Password is required");
//            return;
//        }
        boolean validated = InputValidator.validatePassword(password);
        passwordConfirmEditText.setEnabled(false);
        passwordLayout.setError(null);
        if(!validated){
            passwordLayout.setError("Password should be at least 6 characters");
        } else{
            passwordConfirmEditText.setEnabled(true);
        }
        signUpButtonController.isPasswordValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkPasswordConfirmEditText(String password, String passwordAgain){
        boolean validated = InputValidator.confirmPassword(password, passwordAgain);
        passwordConfirmLayout.setError(null);
        if(!validated){
            passwordConfirmLayout.setError("Passwords don't match");
        }
        signUpButtonController.isPasswordConfirmed = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

//    private SpannableString getAskPolicyText(){
//        String text = "I agree to " + getResources().getString(R.string.app_name) +
//                "'s Terms of Use and I have read and I comply with " +
//                getResources().getString(R.string.app_name) + "'s Privacy Policy";
//        SpannableString sp = new SpannableString(text);
//        int start = text.indexOf("Terms of Use");
//        int end = start + "Terms of Use".length();
//        sp.setSpan(new MyClickableSpan("Terms of Use"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        start = text.indexOf("Privacy Policy");
//        end = start + "Privacy Policy".length();
//        sp.setSpan(new MyClickableSpan("Privacy Policy"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sp;
//    }
//
//    private void addAccountTypeToSPref(boolean forPerson){
//        SharedPreferences sPref = getSharedPreferences(getString(R.string.account_type),
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sPref.edit();
//        if(forPerson){
//            editor.putInt(mAuth.getCurrentUser().getEmail(), AccountTypes.PERSON);
//        }else{
//            editor.putInt(mAuth.getCurrentUser().getEmail(), AccountTypes.RESTAURANT);
//        }
//        editor.apply();
//    }

    private class SignUpButtonController{
        boolean isNameValid = false;
        boolean isEmailValid = false;
        boolean isPasswordValid = false;
        boolean isPasswordConfirmed = false;
        boolean isPolicyChecked = true;
        Button signUpButton;

        SignUpButtonController(Button signUpButton){
            this.signUpButton = signUpButton;
        }
        void enableOrDisableSignUpButton(){
            if(isNameValid && isEmailValid && isPasswordValid && isPasswordConfirmed && isPolicyChecked){
                signUpButton.setEnabled(true);
            } else{
                signUpButton.setEnabled(false);
            }
        }
    }

    private class MyOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.name:
                    if(hasFocus) {
                        Log.i("focus", "got by 'name'");
                    } else{
                        Log.i("focus", "lost by 'name'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_name)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_name)).setError("Name is required");
                        }
                    }
                    break;
                case R.id.email:
                    if(hasFocus) {
                        Log.i("focus", "got by 'email'");
                    } else{
                        Log.i("focus", "lost by 'email'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_email)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_email)).setError("Email is required");
                        }
                    }
                    break;
                case R.id.password:
                    if(hasFocus) {
                        Log.i("focus", "got by 'password'");
                    } else{
                        Log.i("focus", "lost by 'password'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_password)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_password)).setError("Password is required");
                        }
                    }
                    break;
                case R.id.password_confirm:
                    if(hasFocus){
                        Log.i("focus", "got by 'password_confirm'");
                    } else{
                        Log.i("focus", "lost by 'password_confirm'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_password_confirm)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_password_confirm)).setError("Please confirm your password");
                        }
                    }
                    break;
            }
        }
    }

//    private class MyClickableSpan extends ClickableSpan{
//        String link;
//        MyClickableSpan(String link){
//            super();
//            this.link = link;
//        }
//        @Override
//        public void onClick(@NonNull View widget) {
//            Intent intent = new Intent(SignUp.this, Policy.class);
//            if(link.equals("Terms of Use")){
//                Log.i("policy", "terms of use");
//                intent.putExtra("policy_type", PolicyType.TERMS_OF_USE);
//            }else{
//                Log.i("policy", "privacy");
//                intent.putExtra("policy_type", PolicyType.PRIVACY_POLICY);
//            }
//            startActivity(intent);
//        }
//    }
}
