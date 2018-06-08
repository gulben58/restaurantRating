package com.example.gulbe.restaurantratingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainPage extends AppCompatActivity {
    ImageButton search;
    ImageButton find;
    ImageButton profile;
    Button logout;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        search=(ImageButton) findViewById(R.id.searchicon);
        find=(ImageButton) findViewById(R.id.findicon);
        Bundle extras=getIntent().getExtras();
        final int userid=extras.getInt("user_id");
        final String email=extras.getString("user_email");
        sp = getSharedPreferences("login",MODE_PRIVATE);
        logout=(Button) findViewById(R.id.logout2);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainPage.this,MainActivity.class);
                sp.edit().putBoolean("logged",false).apply();
                startActivity(intent);

            }
        });


        profile=(ImageButton) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainPage.this,ProfileActivity.class);
                intent.putExtra("user_id",userid);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });

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
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
