package com.example.gulbe.restaurantratingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RatingandComments extends AppCompatActivity {
    RatingBar rating;
    TextView txt;
    EditText comment;
    Button send;
    int restaurant_id;
    int user_id;
    String com;
    String url;
    String r_name;
    ImageView img;
    TextView info;
    ListView commentText;
    String email;
    ArrayAdapter<String> adapter;
    ArrayList<String> listOfComments;
    ArrayList<String> listOfEmails;
    Button rateBtn;
    ImageButton viewOnMap;
    Double latitude;
    Double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratingand_comments);
        rating = (RatingBar) findViewById(R.id.rating);
        txt = (TextView) findViewById(R.id.text);
        comment = (EditText) findViewById(R.id.comment);
        send = (Button) findViewById(R.id.send);
        img = (ImageView) findViewById(R.id.r_img);
        info = (TextView) findViewById(R.id.info);
        commentText = (ListView) findViewById(R.id.comments);
        listOfComments=new ArrayList<>();
        listOfEmails=new ArrayList<>();
        rateBtn=(Button) findViewById(R.id.rate);
        viewOnMap=(ImageButton) findViewById(R.id.viewMap);



        Bundle extras = getIntent().getExtras();
        restaurant_id = extras.getInt("restaurant_id");
        user_id = extras.getInt("user_id");
        url = extras.getString("images");
        r_name = extras.getString("restaurant_name");
        email = extras.getString("user_email");
        latitude=extras.getDouble("latitude");
        longitude=extras.getDouble("longitude");

        info.append(r_name + "\n");
        info.append("\n");

     viewOnMap.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + " (" + "Where the party is at" + ")";
             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
             intent.setPackage("com.google.android.apps.maps");
             startActivity(intent);
         }
     });
        Picasso.with(getApplicationContext()).load(url).into(img);

        new GetComments().execute();

      rateBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              try {
                  URL url = new URL("http://192.168.128.86/saveRatingToDB.php");
                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                  conn.setRequestMethod("POST");
                  conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                  conn.setRequestProperty("Accept", "application/json");
                  conn.setDoOutput(true);
                  conn.setDoInput(true);

                  JSONObject jsonParam = new JSONObject();
                  jsonParam.put("user_id", user_id);
                  jsonParam.put("ratings",rating.getRating());
                  jsonParam.put("r_id", restaurant_id);

                  Log.i("JSON", jsonParam.toString());
                  DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                  //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                  os.writeBytes(jsonParam.toString());

                  os.flush();
                  os.close();

                  Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                  Log.i("MSG", conn.getResponseMessage());
                  Toast.makeText(RatingandComments.this, "Successful" + jsonParam.toString(), Toast.LENGTH_LONG).show();
                  conn.disconnect();


              } catch (Exception e) {
                  Log.e("ss", "Exception senPost" + e.getMessage());
                  e.printStackTrace();
              }


          }
      });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com = comment.getText().toString();
                try {
                    URL url = new URL("http://192.168.128.86/saveCommentToDB.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("user_id", user_id);
                    jsonParam.put("comment", com);
                    jsonParam.put("r_id", restaurant_id);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    Toast.makeText(RatingandComments.this, "Successful" + jsonParam.toString(), Toast.LENGTH_LONG).show();
                    conn.disconnect();
                    listOfComments.add(com);
                    listOfEmails.add(email);
                    adapter=new RatingCustomAdapter(RatingandComments.this,listOfComments,listOfEmails);
                    commentText.setAdapter(adapter);


                } catch (Exception e) {
                    Log.e("ss", "Exception senPost" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });


    }

    private class GetComments extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.128.86/commentsToJSON.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                getComment(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void getComment(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        String a = "";
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (restaurant_id == obj.getInt("r_id")) {
                listOfComments.add(obj.getString("comments"));
                listOfEmails.add(obj.getString("user_email"));
            }

        }
        adapter=new RatingCustomAdapter(RatingandComments.this,listOfComments,listOfEmails);
        commentText.setAdapter(adapter);
    }

}