package com.productionreport.bd;

import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.PersonStatus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ApproveMember extends AppCompatActivity {

    LinearLayout formLayout;
    ProgressBar progressBar;
    LinearLayout spinnerLayout;
    TextView nameTV;
    RadioButton radioAdmin;
    RadioButton radioSupervisor;
    Spinner spinner;
    Button saveButton;

    private int status = -1;
    private String section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_member);

        String name = getIntent().getStringExtra("name");
        final String personLink = getIntent().getStringExtra("personLink");

        formLayout = findViewById(R.id.form_layout);
        progressBar = findViewById(R.id.progress_bar);
        spinnerLayout = findViewById(R.id.spinner_layout);
        nameTV = findViewById(R.id.name_tv);
        radioAdmin = findViewById(R.id.radio_admin);
        radioSupervisor = findViewById(R.id.radio_supervisor);
        spinner = findViewById(R.id.spinner);
        saveButton = findViewById(R.id.save_button);

        if(name != null){
            nameTV.setText(name);
        }

        radioAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerLayout.setVisibility(View.GONE);
                section = null;
                status = PersonStatus.ADMIN;
                saveButton.setEnabled(true);
            }
        });

        radioSupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerLayout.setVisibility(View.VISIBLE);
                status = PersonStatus.SUPERVISOR;
                saveButton.setEnabled(true);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("section", parent.getItemAtPosition(position).toString());
                section = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Map<String, Object> data = new HashMap<>();
                data.put(FirestoreFieldNames.PERSON_STATUS, status);
                data.put(FirestoreFieldNames.PERSON_SECTION, section);
                FirebaseFirestore.getInstance().collection("person_vital")
                        .document(personLink)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(ApproveMember.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                formLayout.setVisibility(View.VISIBLE);
                                ApproveMember.this.finish();
                            }
                        });
            }
        });
    }
}
