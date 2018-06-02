package com.example.gulbe.restaurantratingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {
    private String valid_email;
    private EditText etEmail;
    @InjectView(R.id.password) EditText etPswd;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ButterKnife.inject(this);
        etEmail = (EditText) findViewById(R.id.email);
        etEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                // TODO Auto-generated method stub
                Is_Valid_Email(etEmail); // pass your EditText Obj here.
            }

            public void Is_Valid_Email(EditText edt) {
                if (edt.getText().toString() == null) {
                    edt.setError("Invalid Email Address");
                    valid_email = null;
                } else if (isEmailValid(edt.getText().toString()) == false) {
                    edt.setError("Invalid Email Address");
                    valid_email = null;
                } else {
                    valid_email = edt.getText().toString();
                }
            }

            boolean isEmailValid(CharSequence email) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches();
            } // end of TextWatcher (email)

        });

        etPswd = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.signin);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getJSON("http://192.168.128.86/loginJSON.php");
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                progressDialog.dismiss();
                try {
                    checkLogin(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            protected String doInBackground(Void... voids) {



                try {

                    URL url = new URL(urlWebService);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;


                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    private void checkLogin(String json) throws JSONException {



        int count=0;
        JSONArray jsonArray = new JSONArray(json);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String email = obj.getString("user_email");
                String pswd = obj.getString("user_password");
                int userid=obj.getInt("user_id");
                String mail = etEmail.getText().toString();
                String pw = etPswd.getText().toString();

                if (mail.equals(email) && pw.equals(pswd)) {
                   // Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(MainActivity.this,MainPage.class);
                    intent.putExtra("user_id",userid);
                    intent.putExtra("user_email",mail);
                    startActivity(intent);

                    count++;



                }



            }

        }
        if(count==0){
            Toast.makeText(MainActivity.this, "Wrong Email or Password", Toast.LENGTH_LONG).show();

        }


    }
}

