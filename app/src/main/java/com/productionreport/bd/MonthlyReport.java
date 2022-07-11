package com.productionreport.bd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class MonthlyReport extends AppCompatActivity {

    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    String mYearString;
    String mMonthString;
    String mDateString;
    int year;
    int month;

    Spinner yearSpinner;
    Spinner monthSpinner;
    Button okayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        yearSpinner = findViewById(R.id.year_spinner);
        monthSpinner = findViewById(R.id.month_spinner);
        okayButton = findViewById(R.id.okay_button);

        bindYearSpinner();

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyReport.this, MonthlyReportResult.class);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivity(intent);
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

                }else{

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
