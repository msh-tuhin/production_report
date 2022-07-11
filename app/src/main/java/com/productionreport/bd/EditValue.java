package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditValue extends AppCompatActivity {

    LinearLayout formLayout;
    ProgressBar progressBar;
    TextInputEditText editTV;
    Button saveButton;
    Long count;
    String dataID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_value);

        count = getIntent().getLongExtra("count", 0L);
        dataID = getIntent().getStringExtra("id");

        formLayout = findViewById(R.id.form_layout);
        progressBar = findViewById(R.id.progress_bar);
        editTV = findViewById(R.id.edit_tv);
        saveButton = findViewById(R.id.save_button);

        editTV.setText(Long.toString(count));
        saveButton.setEnabled(false);

        editTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!s.toString().isEmpty());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                formLayout.setVisibility(View.GONE);
                FirebaseFirestore.getInstance().collection("data")
                        .document(dataID)
                        .update(FirestoreFieldNames.DATA_COUNT, Integer.valueOf(editTV.getText().toString()))
                        .addOnSuccessListener(EditValue.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditValue.this, "Data Update Successful", Toast.LENGTH_SHORT)
                                        .show();
                                EditValue.this.finish();
                            }
                        })
                        .addOnFailureListener(EditValue.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                formLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditValue.this, "Data Update Failed", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });
    }
}
