package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.PersonStatus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterBusiness extends AppCompatActivity {

    TextInputEditText nameEditText;
    TextInputEditText addressEditText;
    Button saveButton;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);

        nameEditText = findViewById(R.id.name_edittext);
        addressEditText = findViewById(R.id.address_edittext);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.linearLayout);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                if(address.trim().isEmpty()) address = "";

                Map<String, String> fData = new HashMap<>();
                fData.put(FirestoreFieldNames.FACTORY_NAME, name);
                fData.put(FirestoreFieldNames.FACTORY_ADDRESS, address);
                fData.put(FirestoreFieldNames.FACTORY_CODE, "rnsco");
                fData.put(FirestoreFieldNames.FACTORY_CREATOR, FirebaseAuth.getInstance().getCurrentUser().getUid());

                final DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("factory_vital").document();
                docRef.set(fData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SharedPreferences sPref = getSharedPreferences(getString(R.string.vital_info),
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sPref.edit();
                                editor.putString("person_mof", docRef.getId());
                                editor.putLong("person_status", PersonStatus.CREATOR);
                                editor.apply();
                                Intent intent = new Intent(RegisterBusiness.this, HomeCreator.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(RegisterBusiness.this, "Something wrong, try again!", Toast.LENGTH_SHORT)
                                .show();
                            }
                        });
            }
        });
    }
}
