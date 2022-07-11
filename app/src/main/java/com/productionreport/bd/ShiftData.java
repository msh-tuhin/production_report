package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import models.DataModel;
import myapp.utils.FirestoreFieldNames;
import viewholders.HourlyItemHolder;
import viewholders.ShiftItemHolder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShiftData extends AppCompatActivity {

    ShiftAdapter adapter;
    RecyclerView rv;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    String section;
    String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_data);

        section = getIntent().getStringExtra("section");
        orderID = getIntent().getStringExtra("order_id");
        Log.i("section", section);
        Log.i("order_id", orderID);

        progressBar = findViewById(R.id.progress_bar);
        // swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = new ShiftAdapter(new ArrayList<String>());
        rv.setAdapter(adapter);
        populateAdapter();

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                adapter = new ShiftAdapter(new ArrayList<String>());
//                rv.setAdapter(adapter);
//                populateAdapter();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    private void populateAdapter(){
        FirebaseFirestore.getInstance().collection("data")
                .whereEqualTo(FirestoreFieldNames.DATA_ORDER_LINK, orderID)
                .whereEqualTo(FirestoreFieldNames.DATA_SECTION, section)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        int shift1 = 0;
                        int shift2 = 0;
                        int shift3 = 0;
                        int shift4 = 0;
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                DataModel dataModel = documentSnapshot.toObject(DataModel.class);
                                switch (dataModel.getTimeBucket().intValue()){
                                    case 8:
                                    case 9:
                                    case 10:
                                    case 11:
                                    case 12:
                                        shift1+=dataModel.getCount().intValue();
                                        break;
                                    case 14:
                                    case 15:
                                    case 16:
                                        shift2+=dataModel.getCount().intValue();
                                        break;
                                    case 17:
                                    case 18:
                                    case 19:
                                    case 20:
                                        shift3+=dataModel.getCount().intValue();
                                        break;
                                    case 22:
                                    case 23:
                                    case 0:
                                    case 1:
                                    case 2:
                                        shift4+=dataModel.getCount().intValue();
                                        break;
                                }
                            }
                        }
                        adapter.shifts.add(Integer.toString(shift1));
                        adapter.shifts.add(Integer.toString(shift2));
                        adapter.shifts.add(Integer.toString(shift3));
                        adapter.shifts.add(Integer.toString(shift4));
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private class ShiftAdapter extends RecyclerView.Adapter<ShiftItemHolder>{

        ArrayList<String> shifts = new ArrayList<>();

        ShiftAdapter(ArrayList<String> shifts){
            this.shifts.addAll(shifts);
        }

        @NonNull
        @Override
        public ShiftItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hourly_item, parent, false);
            return new ShiftItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShiftItemHolder holder, int position) {
            holder.bindTo(position, shifts.get(position));
        }

        @Override
        public int getItemCount() {
            return shifts.size();
        }
    }
}
