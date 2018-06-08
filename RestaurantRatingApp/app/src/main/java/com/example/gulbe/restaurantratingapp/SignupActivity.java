package com.example.gulbe.restaurantratingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gulbe on 28.03.2018.
 */

public class SignupActivity extends AppCompatActivity {
    private EditText temail;
    private EditText tpswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        temail = (EditText) findViewById(R.id.email);
        tpswd = (EditText) findViewById(R.id.password);


        Button button3 = (Button) findViewById(R.id.signup);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendPost();


            }





        });

    }
    public void sendPost() {


        final String usrmail=temail.getText().toString();
        final String usrpswd=tpswd.getText().toString();

        try {
            URL url = new URL("http://192.168.128.86/signUp.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email",usrmail);
            jsonParam.put("password",usrpswd);

            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());
           // Toast.makeText(SignupActivity.this, "Successful"+jsonParam.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_LONG).show();

            conn.disconnect();
            Intent intent=new Intent(SignupActivity.this,MainActivity.class);

            startActivity(intent);

        } catch (Exception e) {
            Log.e("ss","Exception senPost" + e.getMessage());
            e.printStackTrace();
        }


    }

}
