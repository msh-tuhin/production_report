package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import myapp.utils.FirestoreFieldNames;
import viewholders.DateItemHolder;
import viewholders.MemberItemHolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SectionDetailSupervisor extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv;
    DatesAdapter adapter;
    String section;
    String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secton_detail_supervisor);

        section = getIntent().getStringExtra("section");
        orderID = getIntent().getStringExtra("order_id");
        Log.i("section", section);
        Log.i("order_id", orderID);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = new DatesAdapter(new ArrayList<String>());
        rv.setAdapter(adapter);
        // populateAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new DatesAdapter(new ArrayList<String>());
                rv.setAdapter(adapter);
                populateAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SectionDetailSupervisor.this, AddData.class);
                intent.putExtra("section", section);
                intent.putExtra("order_id", orderID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateAdapter();
    }

    private void populateAdapter(){
        FirebaseFirestore.getInstance().collection("dates_" + section)
                .document(orderID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> dates = (ArrayList<String>)documentSnapshot
                                        .get(FirestoreFieldNames.DATA_ADD_DATES);
                                adapter.dates.clear();
                                adapter.dates.addAll(dates);
                                adapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private class DatesAdapter extends RecyclerView.Adapter<DateItemHolder>{

        ArrayList<String> dates = new ArrayList<>();

        DatesAdapter(ArrayList<String> members){
            this.dates.addAll(members);
        }

        @NonNull
        @Override
        public DateItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.date_item, parent, false);
            return new DateItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DateItemHolder holder, int position) {
            holder.bindTo(SectionDetailSupervisor.this, dates.get(position), section, orderID);
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }
}
