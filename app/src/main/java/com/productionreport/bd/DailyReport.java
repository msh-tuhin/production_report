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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class DailyReport extends AppCompatActivity {

    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    String mYearString;
    String mMonthString;
    String mDateString;
    int year;
    int month;
    int date;

    Spinner yearSpinner;
    Spinner monthSpinner;
    Spinner dateSpinner;
    Button okayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        yearSpinner = findViewById(R.id.year_spinner);
        monthSpinner = findViewById(R.id.month_spinner);
        dateSpinner = findViewById(R.id.date_spinner);
        okayButton = findViewById(R.id.okay_button);

        bindYearSpinner();

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyReport.this, DailyReportResult.class);
                intent.putExtra("full_date", getFullDate());
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
