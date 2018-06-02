package com.example.gulbe.restaurantratingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainPage extends AppCompatActivity {
    ImageButton search;
    ImageButton find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        search=(ImageButton) findViewById(R.id.searchicon);
        find=(ImageButton) findViewById(R.id.findicon);
        Bundle extras=getIntent().getExtras();
        final int userid=extras.getInt("user_id");
        final String email=extras.getString("user_email");




        find.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainPage.this,Success_Activity.class);
                        intent.putExtra("user_id",userid);
                        intent.putExtra("user_email",email);
                        startActivity(intent);



                    }
                }
        );
        search.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainPage.this,HomePage.class);
                        intent.putExtra("user_id",userid);
                        intent.putExtra("user_email",email);
                        startActivity(intent);



                    }
                }
        );
    }
}
