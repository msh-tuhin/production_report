package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import models.DataModel;
import models.OrderModel;
import myapp.utils.FirestoreFieldNames;
import viewholders.HourlyItemHolder;
import viewholders.OrderItemHolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Hourly extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv;
    FirestorePagingAdapter<DataModel, HourlyItemHolder> adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String section;
    String orderID;
    String fullDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly);

        section = getIntent().getStringExtra("section");
        orderID = getIntent().getStringExtra("order_id");
        fullDate = getIntent().getStringExtra("full_date");

        Log.i("section", section);
        Log.i("order_id", orderID);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv = findViewById(R.id.rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = getAdapter(this);
        rv.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_shift, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.show_shift){
            Intent intent = new Intent(Hourly.this, ShiftData.class);
            intent.putExtra("section", getIntent().getStringExtra("section"));
            intent.putExtra("order_id", getIntent().getStringExtra("order_id"));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private FirestorePagingAdapter<DataModel, HourlyItemHolder> getAdapter(LifecycleOwner lifecycleOwner){
        Query bQuery = db.collection("data")
                .whereEqualTo(FirestoreFieldNames.DATA_ORDER_LINK, orderID)
                .whereEqualTo(FirestoreFieldNames.DATA_SECTION, section)
                .whereEqualTo(FirestoreFieldNames.DATA_FULL_DATE, fullDate)
                .orderBy(FirestoreFieldNames.DATA_TIMEBUCKET, Query.Direction.ASCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<DataModel> options = new FirestorePagingOptions.Builder<DataModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, DataModel.class).build();
        FirestorePagingAdapter<DataModel, HourlyItemHolder> adapter =
                new FirestorePagingAdapter<DataModel, HourlyItemHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HourlyItemHolder hourlyItemHolder, int i, @NonNull DataModel dataModel) {
                        hourlyItemHolder.bindTo(Hourly.this, dataModel, this.getCurrentList().get(i).getId());
                    }

                    @NonNull
                    @Override
                    public HourlyItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.hourly_item, parent, false);
                        return new HourlyItemHolder(view);
                    }
                };
        return adapter;
    }
}
