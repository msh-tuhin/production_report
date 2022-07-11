package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import myapp.utils.FirestoreFieldNames;
import viewholders.MemberItemHolder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AllMembers extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MemberAdapter adapter;
    RecyclerView rv;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = new MemberAdapter(new ArrayList<String>());
        rv.setAdapter(adapter);
        populateAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new MemberAdapter(new ArrayList<String>());
                rv.setAdapter(adapter);
                populateAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void populateAdapter(){
        db.collection("person_vital")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        if(documentSnapshot.exists()){
                            String factory = documentSnapshot.getString(FirestoreFieldNames.PERSON_MEMBER_OF);
                            if(factory!=null){
                                fetchMembersList(factory);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void fetchMembersList(String factory){
        if(factory == null) return;
        db.collection("members_list")
                .document(factory)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> members = (ArrayList<String>)documentSnapshot
                                        .get(FirestoreFieldNames.MEMBERS_LIST);
                                adapter.members.addAll(members);
                                adapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private class MemberAdapter extends RecyclerView.Adapter<MemberItemHolder>{

        ArrayList<String> members = new ArrayList<>();

        MemberAdapter(ArrayList<String> members){
            this.members.addAll(members);
        }

        @NonNull
        @Override
        public MemberItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.member_item, parent, false);
            return new MemberItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MemberItemHolder holder, int position) {
            holder.bindTo(AllMembers.this, members.get(position));
        }

        @Override
        public int getItemCount() {
            return members.size();
        }
    }
}
