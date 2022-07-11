package com.productionreport.bd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreateJoinFactory extends AppCompatActivity {

    TextView registerBusiness;
    TextView joinBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_factory);

        registerBusiness = findViewById(R.id.register_tv);
        joinBusiness =findViewById(R.id.join_tv);

        registerBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateJoinFactory.this, RegisterBusiness.class));
            }
        });

        joinBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateJoinFactory.this, JoinBusiness.class));
            }
        });
    }
}
