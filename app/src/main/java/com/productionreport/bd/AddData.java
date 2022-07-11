package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.FirestoreFieldNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddData extends AppCompatActivity {

    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    String mYearString;
    String mMonthString;
    String mDateString;
    int year;
    int month;
    int date;
    int timeBucket;

    LinearLayout formLayout;
    Spinner yearSpinner;
    Spinner monthSpinner;
    Spinner dateSpinner;
    Spinner timeSpinner;
    TextInputEditText countTV;
    Button saveButton;
    ProgressBar progressBar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        final String section = getIntent().getStringExtra("section");
        final String orderID = getIntent().getStringExtra("order_id");
        Log.i("section", section);
        Log.i("order_id", orderID);

        formLayout = findViewById(R.id.form_layout);
        yearSpinner = findViewById(R.id.year_spinner);
        monthSpinner = findViewById(R.id.month_spinner);
        dateSpinner = findViewById(R.id.date_spinner);
        timeSpinner = findViewById(R.id.time_spinner);
        countTV = findViewById(R.id.count_tv);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);

        bindYearSpinner();
        bindTimeSpinner();

        countTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                SharedPreferences sPref = getSharedPreferences(getString(R.string.vital_info),
                        Context.MODE_PRIVATE);
                String factory = sPref.getString("person_mof", null);
                int count = Integer.valueOf(countTV.getText().toString());
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("l", orderID);
                Map<String, Object> data = new HashMap<>();
                data.put(FirestoreFieldNames.DATA_COUNT, count);
                data.put(FirestoreFieldNames.DATA_TIMESTAMP, new Date());
                data.put(FirestoreFieldNames.DATA_ADDED_BY, mAuth.getCurrentUser().getUid());
                data.put(FirestoreFieldNames.DATA_FACTORY_LINK, factory);
                data.put(FirestoreFieldNames.DATA_SECTION, section);
                data.put(FirestoreFieldNames.DATA_ORDER_MAP, orderData);
                data.put(FirestoreFieldNames.DATA_YEAR, year);
                data.put(FirestoreFieldNames.DATA_MONTH, month);
                data.put(FirestoreFieldNames.DATA_DATE, date);
                data.put(FirestoreFieldNames.DATA_TIMEBUCKET, timeBucket);
                data.put(FirestoreFieldNames.DATA_FULL_DATE, getFullDate());
                db.collection("data")
                        .add(data)
                        .addOnSuccessListener(AddData.this, new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                AddData.this.finish();
                            }
                        })
                        .addOnFailureListener(AddData.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                formLayout.setVisibility(View.VISIBLE);
                            }
                        });

                db.collection("dates_"+ section).document(orderID)
                        .update(FirestoreFieldNames.DATA_ADD_DATES, FieldValue.arrayUnion(getFullDate()));
            }
        });

    }

    private void bindYearSpinner(){
        ArrayList<String> yearArrayList = new ArrayList<>();
        //yearArrayList.add("Year");
        for(int i=2020; i<=2025; i++){
            yearArrayList.add(Integer.toString(i));
        }
        //yearArrayList.add("2020");
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, yearArrayList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("year", parent.getItemAtPosition(position).toString());
                mYearString = parent.getItemAtPosition(position).toString();
                year = Integer.valueOf(mYearString);
//                if(mYearString.equals("Year")){
//                    bindMonthSpinner();
//                    monthSpinner.setEnabled(false);
//                    bindDateSpinner();
//                    dateSpinner.setEnabled(false);
//                }else{
//                    monthSpinner.setEnabled(true);
//                    bindMonthSpinner();
//                }
                monthSpinner.setEnabled(true);
                bindMonthSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindMonthSpinner(){
        // monthLayout.setEnabled(false);
        ArrayList<String> monthArrayList = new ArrayList<>(Arrays.asList(months));
        monthArrayList.add(0, "Month");
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monthArrayList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("month", parent.getItemAtPosition(position).toString());
                mMonthString = parent.getItemAtPosition(position).toString();
                month = position;
                if(mMonthString.equals("Month")){
                    bindDateSpinner();
                    dateSpinner.setEnabled(false);
                }else{
                    dateSpinner.setEnabled(true);
                    bindDateSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindDateSpinner(){
        ArrayList<String> dateArrayList = new ArrayList<>();
        dateArrayList.add("Date");
        if(!mYearString.equals("Year") && !mMonthString.equals("Month")){
            for(int i=1; i<=28; i++){
                dateArrayList.add(Integer.toString(i));
            }

            if(mMonthString.equals("Feb")){
                if(isLeapYear(Integer.valueOf(mYearString))){
                    dateArrayList.add("29");
                }
            }else{
                switch(mMonthString){
                    case "Jan":
                    case "March":
                    case "May":
                    case "July":
                    case "Aug":
                    case "Oct":
                    case "Dec":
                        dateArrayList.add("29");
                        dateArrayList.add("30");
                        dateArrayList.add("31");
                        break;
                    default:
                        dateArrayList.add("29");
                        dateArrayList.add("30");
                        break;
                }
            }
        }
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dateArrayList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("date", parent.getItemAtPosition(position).toString());
                mDateString = parent.getItemAtPosition(position).toString();
                date = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindTimeSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_slots, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeBucket = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isLeapYear(int year){
        return ((year%4 == 0 && year%100 != 0) || (year%4 == 0 && year%100 == 0 && year%400 == 0));
    }

    private String getFullDate(){
        String dateString = Integer.toString(date);
        if(date<=9){
            dateString = "0" + date;
        }

        String monthString = Integer.toString(month);
        if(month<=9){
            monthString = "0" + month;
        }

        String yearString = Integer.toString(year);

        return dateString + "-" + monthString + "-" + yearString;
    }
}
