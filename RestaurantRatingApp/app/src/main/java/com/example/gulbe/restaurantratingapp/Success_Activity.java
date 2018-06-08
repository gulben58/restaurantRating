package com.example.gulbe.restaurantratingapp;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Success_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private LocationTracker tracker;
    ArrayList<Float> listOfDistances;
    ArrayList<Integer> listOfRestaurantIds;
    ArrayList<String> listOfNames;
    ArrayList<String> listOfImages;
    ArrayList<Double> listOfLatitudes;
    ArrayList<Double> listOfLongitudes;
    ArrayList<Float> rating;
    ArrayList<String> r_name;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    Double latitude1;
    Double latitude2;
    Double longitude1;
    Double longitude2;
    int userid;
    String email;
    SharedPreferences sp;
    Button logout;
    private SwipeRefreshLayout swipeRefreshLayout;
    float dist;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.success_activity);

        listOfNames=new ArrayList<>();
        listOfImages=new ArrayList<>();
        listOfLatitudes=new ArrayList<>();
        listOfLongitudes=new ArrayList<>();
        listOfRestaurantIds=new ArrayList<>();
        listOfDistances=new ArrayList<>();
        r_name=new ArrayList<>();
        rating=new ArrayList<>();
        Bundle extras=getIntent().getExtras();
        userid=extras.getInt("user_id");
        email=extras.getString("user_email");
        sp = getSharedPreferences("login",MODE_PRIVATE);
        logout=(Button) findViewById(R.id.logout3);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Success_Activity.this,MainActivity.class);
                sp.edit().putBoolean("logged",false).apply();
                startActivity(intent);

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);




        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        new setRating().execute();
        new RestaurantListView().execute();





    }

    @Override
    public void onRefresh() {
        arrayAdapter.clear();
        new setRating().execute();
        new RestaurantListView().execute();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }


    }


    private class setRating extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        @Override
        protected String doInBackground(Void... voids) {


            try {
                URL url = new URL("http://192.168.128.86/ratingsToJSON.php");
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
                load(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void load(String json) throws JSONException {


        JSONArray jsonArray = new JSONArray(json);
        int count = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            r_name.add(obj.getString("r_name"));
            rating.add((float) obj.getDouble("ratings"));



        }









    }


    private class RestaurantListView extends AsyncTask<Void, Void, String> {
        final ProgressDialog progressDialog = new ProgressDialog(Success_Activity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {



            try {
                URL url = new URL("http://192.168.128.86/restaurantHTMLtoJSON.php");
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
            progressDialog.dismiss();

            try {
                getLocation(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    }
    private void getLocation(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);

        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            latitude1 = obj.getDouble("latitude");
            longitude1 = obj.getDouble("longitude");
            tracker = new LocationTracker(Success_Activity.this);
            if (tracker.canGetLocation()) {
                latitude2 = tracker.getLatitude();
                longitude2 = tracker.getLongitude();
              /*  Double theta = longitude1 - longitude2;
                Double dist = Math.sin(deg2rad(latitude1))
                        * Math.sin(deg2rad(latitude2))
                        + Math.cos(deg2rad(latitude1))
                        * Math.cos(deg2rad(latitude2))
                        * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;*/
                Location loc1 = new Location("");
                loc1.setLatitude(latitude1);
                loc1.setLongitude(longitude1);

                Location loc2 = new Location("");
                loc2.setLatitude(latitude2);
                loc2.setLongitude(longitude2);

                dist = loc1.distanceTo(loc2)/1000;


                if (dist <= 5.0) {


                    listOfImages.add(obj.getString("image"));
                    listOfNames.add(obj.getString("name"));
                    listOfRestaurantIds.add(obj.getInt("id"));
                    listOfDistances.add(dist);
                    listOfLatitudes.add(latitude1);
                    listOfLongitudes.add(longitude1);

                }

            } else {
                tracker.showSettingsAlert();
            }
            listView = (ListView) findViewById(R.id.lv);
            arrayAdapter = new CustomAdapter(Success_Activity.this, listOfNames, listOfImages, listOfDistances,rating,r_name);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Success_Activity.this, RatingandComments.class);
                    intent.putExtra("restaurant_id", listOfRestaurantIds.get(position));
                    intent.putExtra("user_id", userid);
                    intent.putExtra("images", listOfImages.get(position));
                    intent.putExtra("restaurant_name", listOfNames.get(position));
                    intent.putExtra("user_email", email);
                    intent.putExtra("latitude",listOfLatitudes.get(position));
                    intent.putExtra("longitude",listOfLongitudes.get(position));

                    startActivity(intent);
                }
            });
            arrayAdapter.notifyDataSetChanged();


        }

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
