package com.productionreport.bd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import models.OrderModel;
import myapp.utils.FirestoreFieldNames;
import viewholders.OrderItemHolder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.Query;

public class ProductionFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestorePagingAdapter<OrderModel, OrderItemHolder> adapter;
    LinearLayoutManager linearLayoutManager;

    public ProductionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_production, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rv = view.findViewById(R.id.orders_rv);

        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = getAdapter(this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddOrder.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private FirestorePagingAdapter<OrderModel, OrderItemHolder> getAdapter(LifecycleOwner lifecycleOwner){
        SharedPreferences sPref = getActivity().getSharedPreferences(getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        String factory = sPref.getString("person_mof", "random_value");
        // String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query bQuery = db.collection("orders")
                .whereEqualTo(FirestoreFieldNames.ORDER_FACTORY_LINK, factory)
                .orderBy(FirestoreFieldNames.ORDER_ADD_TIME, Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<OrderModel> options = new FirestorePagingOptions.Builder<OrderModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, OrderModel.class).build();

        FirestorePagingAdapter<OrderModel, OrderItemHolder> adapter =
                new FirestorePagingAdapter<OrderModel, OrderItemHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderItemHolder orderItemHolder, int i, @NonNull OrderModel orderModel) {
                        orderItemHolder.bindTo(orderModel, ProductionFragment.this.getActivity(),
                                this.getCurrentList().get(i).getId());
                    }

                    @NonNull
                    @Override
                    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.order_item, parent, false);
                        return new OrderItemHolder(view);
                    }
                };
        return adapter;
    }
}
