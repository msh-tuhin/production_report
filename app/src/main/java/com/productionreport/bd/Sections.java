package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.PersonStatus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sections extends AppCompatActivity {

    ListView sectionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);

        final String orderID = getIntent().getStringExtra("order_id");
        sectionsListView = findViewById(R.id.sections_listView);
        SharedPreferences sPref = getSharedPreferences(getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        Long status = sPref.getLong("person_status", -1L);
        if(status==-1) this.finish();
        if(status==PersonStatus.SUPERVISOR){
            ArrayList<String> sectionList = new ArrayList<>();
            String section = sPref.getString("person_section", null);
            if(section==null) this.finish();
            sectionList.add(section);
            MyArrayAdapter adapter = new MyArrayAdapter(this,
                    android.R.layout.simple_list_item_1, sectionList);
            sectionsListView.setAdapter(adapter);

            sectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Sections.this, SectionDetailSupervisor.class);
                    String section = ((TextView)view.findViewById(R.id.item_name)).getText().toString();
                    intent.putExtra("section", section);
                    intent.putExtra("order_id", orderID);
                    startActivity(intent);
                }
            });
        }else{
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.sections));
            ArrayList<String> sectionsList = new ArrayList<>(list);
            MyArrayAdapter adapter = new MyArrayAdapter(this,
                    android.R.layout.simple_list_item_1, sectionsList);
            sectionsListView.setAdapter(adapter);

            sectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Sections.this, SectionDetailCreator.class);
                    String section = ((TextView)view.findViewById(R.id.item_name)).getText().toString();
                    intent.putExtra("section", section);
                    intent.putExtra("order_id", orderID);
                    startActivity(intent);
                }
            });
        }
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
