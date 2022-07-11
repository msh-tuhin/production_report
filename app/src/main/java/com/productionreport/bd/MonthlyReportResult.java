package com.productionreport.bd;

import androidx.appcompat.app.AppCompatActivity;
import models.DataModel;
import myapp.utils.FirestoreFieldNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyReportResult extends AppCompatActivity {

    int month;
    int year;
    TextView reportTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report_result);

        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        if(month==0 || year==0) return;

        reportTV = findViewById(R.id.report_tv);

        SharedPreferences sPref = getSharedPreferences(getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        String factory = sPref.getString("person_mof", null);
        if(factory==null) return;

        final List<String> orders = new ArrayList<>();
        final List<String> orderNames = new ArrayList<>();
        final List<Map<String, Object>> maps = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("data")
                .whereEqualTo(FirestoreFieldNames.DATA_FACTORY_LINK, factory)
                .whereEqualTo(FirestoreFieldNames.DATA_MONTH, month)
                .whereEqualTo(FirestoreFieldNames.DATA_YEAR, year)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                DataModel dataModel = documentSnapshot.toObject(DataModel.class);
                                String order = (String) dataModel.getOrder().get("l");
                                String orderName = (String) dataModel.getOrder().get("n");
                                String section = dataModel.getSection();
                                int count = dataModel.getCount().intValue();
                                int index = orders.indexOf(order);
                                if(index<0){
                                    orders.add(order);
                                    orderNames.add(orderName);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(section, count);
                                    maps.add(map);
                                }else{
                                    Map<String, Object> map = maps.get(index);
                                    if(map.containsKey(section)){
                                        int totalCount = (int) map.get(section);
                                        map.put(section, totalCount+count);
                                    }else{
                                        map.put(section, count);
                                    }
                                }
                            }

                            String report = "";
                            int i=0;
                            for(Map<String, Object> map : maps){
                                String orderName = orderNames.get(i);
                                report += orderName + "\n\n";
                                if(map.containsKey("Winding")){
                                    report += "    " + "Winding: " + map.get("Winding") + "\n";
                                }
                                if(map.containsKey("Knitting")){
                                    report += "    " + "Knitting: " + map.get("Knitting") + "\n";
                                }
                                if(map.containsKey("Linking")){
                                    report += "    " + "Linking: " + map.get("Linking") + "\n";
                                }
                                if(map.containsKey("2nd_Inspection")){
                                    report += "    " + "2nd_Inspection: " + map.get("2nd_Inspection") + "\n";
                                }
                                if(map.containsKey("Trimming_Mending")){
                                    report += "    " + "Trimming_Mending: " + map.get("Trimming_Mending") + "\n";
                                }
                                if(map.containsKey("Iron_Wash")){
                                    report += "    " + "Iron_Wash: " + map.get("Iron_Wash") + "\n";
                                }
                                if(map.containsKey("Label")){
                                    report += "    " + "Label: " + map.get("Label") + "\n";
                                }
                                if(map.containsKey("Quality")){
                                    report += "    " + "Quality_Assurance: " + map.get("Quality") + "\n";
                                }
                                if(map.containsKey("Packing")){
                                    report += "    " + "Packing: " + map.get("Packing") + "\n";
                                }
                                if(map.containsKey("Carton")){
                                    report += "    " + "Carton: " + map.get("Carton") + "\n";
                                }

                                report+="\n\n";
                                i++;
                            }
                            reportTV.setText(report);
                        }
                    }
                });

    }
}
