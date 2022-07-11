package com.productionreport.bd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.PersonStatus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePageSelector extends AppCompatActivity {

    LinearLayout linearLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_selector);

        linearLayout = findViewById(R.id.linearLayout);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("person_vital")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            SharedPreferences sPref = getSharedPreferences(getString(R.string.vital_info),
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sPref.edit();
                            editor.putString("person_mof", documentSnapshot.getString(FirestoreFieldNames.PERSON_MEMBER_OF));
                            editor.putLong("person_status", (Long) documentSnapshot.get(FirestoreFieldNames.PERSON_STATUS));
                            editor.putString("person_section", documentSnapshot.getString(FirestoreFieldNames.PERSON_SECTION));
                            editor.apply();
                            Long status = (Long) documentSnapshot.get(FirestoreFieldNames.PERSON_STATUS);
                            if(status == null){
                                showDialog();
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                return;
                            }
                            if(status==PersonStatus.PENDING){
                                showDialog();
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }else if(status==PersonStatus.CREATOR || status==PersonStatus.ADMIN){
                                Intent intent = new Intent(HomePageSelector.this, HomeCreator.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(HomePageSelector.this, HomeSupervisor.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page_selector, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomePageSelector.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(){
        String message = "Your membership approval is pending. Please check back later.";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }
}
