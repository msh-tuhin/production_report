package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.PersonStatus;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinBusiness extends AppCompatActivity {

    TextInputEditText businessIdEditText;
    Button okayButton;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_business);

        businessIdEditText = findViewById(R.id.uid);
        okayButton = findViewById(R.id.okay_button);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.linearLayout);

        businessIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                okayButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                String businessId = businessIdEditText.getText().toString();
                FirebaseFirestore.getInstance().collection("factory_vital")
                        .whereEqualTo(FirestoreFieldNames.FACTORY_CODE, businessId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                if(queryDocumentSnapshots.isEmpty()){
                                    Toast.makeText(JoinBusiness.this, "Wrong id! Try again.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    addMember(queryDocumentSnapshots);
                                    Intent intent = new Intent(JoinBusiness.this, HomePageSelector.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(JoinBusiness.this, "Something went wrong! Try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void addMember(QuerySnapshot queryDocumentSnapshots){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for(DocumentSnapshot snap: queryDocumentSnapshots){
            FirebaseFirestore.getInstance()
                    .collection("members_list")
                    .document(snap.getId())
                    .update(FirestoreFieldNames.MEMBERS_LIST, FieldValue.arrayUnion(uid));

            Map<String, Object> pData = new HashMap<>();
            pData.put(FirestoreFieldNames.PERSON_STATUS, PersonStatus.PENDING);
            pData.put(FirestoreFieldNames.PERSON_MEMBER_OF, snap.getId());
            FirebaseFirestore.getInstance()
                    .collection("person_vital")
                    .document(uid)
                    .set(pData, SetOptions.merge());
        }
    }
}
