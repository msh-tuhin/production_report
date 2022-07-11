package com.productionreport.bd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MoreOptionsFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MoreOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Members");
        arrayList.add("Daily Report");
        arrayList.add("Monthly Report");
        arrayList.add("Logout");
        ListView optionsListView = view.findViewById(R.id.options_listView);
        MyArrayAdapter adapter = new MyArrayAdapter(this.requireContext(),
                android.R.layout.simple_list_item_1, arrayList);
        optionsListView.setAdapter(adapter);

        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity parentActivity = MoreOptionsFragment.this.getActivity();
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(parentActivity, AllMembers.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(parentActivity, DailyReport.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(parentActivity, MonthlyReport.class);
                        startActivity(intent);
                        break;
                    case 3:
                        mAuth.signOut();
                        intent = new Intent(parentActivity, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private class MyArrayAdapter extends ArrayAdapter {

        MyArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.more_options_item, parent, false);
            TextView itemName = view.findViewById(R.id.item_name);
            itemName.setText((String)this.getItem(position));
            return view;
        }
    }
}
