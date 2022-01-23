package com.example.sagar.birthday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
   RelativeLayout start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start = findViewById(R.id.START);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        String id = prefs.getString("accid", "Null");//"No name defined" is the default value.
        if (!id.equals("Null")){
            Intent i = new Intent(StartActivity.this,MainActivity.class);
            startActivity(i);
        }
    }
}