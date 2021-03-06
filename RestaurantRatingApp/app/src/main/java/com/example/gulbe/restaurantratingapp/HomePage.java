package com.example.gulbe.restaurantratingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    Button searchBtn;
    EditText searchText;
    ArrayList<String> listOfNames;
    ArrayList<String> listOfImages;
    ArrayList<Integer> listOfRestaurantIds;
    ArrayList<Float> rating;
    ArrayList<String> r_name;
    ArrayList<Double> listOfLatitudes;
    ArrayList<Double> listOfLongitudes;
    Double latitude1;
    Double latitude2;
    Double longitude1;
    Double longitude2;
    private LocationTracker tracker;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    int userid;
    String email;
    ArrayList<Float> listOfDistances;
    Float dist;
    Button logout;
    SharedPreferences sp;
    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         count = 0;
        sp = getSharedPreferences("login",MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        userid = extras.getInt("user_id");
        email = extras.getString("user_email");
        searchText = (EditText) findViewById(R.id.srchTxt);
        listView = (ListView) findViewById(R.id.liv);
        listOfNames = new ArrayList<>();
        listOfImages = new ArrayList<>();
        listOfRestaurantIds = new ArrayList<>();
        listOfDistances = new ArrayList<>();
        r_name=new ArrayList<>();
        rating=new ArrayList<>();
        listOfLatitudes=new ArrayList<>();
        listOfLongitudes=new ArrayList<>();
        logout=(Button) findViewById(R.id.logout) ;
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomePage.this,MainActivity.class);
                sp.edit().putBoolean("logged",false).apply();
                startActivity(intent);


            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listView.setAdapter(null);
                listOfImages.clear();
                listOfNames.clear();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBtn = (Button) findViewById(R.id.srch);
        searchBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        listView.setAdapter(null);
                        listOfImages.clear();
                        listOfNames.clear();

                        new setRating().execute();
                        RestaurantSearch rs;
                        rs = new RestaurantSearch();
                        rs.execute();
                    }
                }
        );
        new setRating().execute();
        new loadRestaurant().execute();


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


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            r_name.add(obj.getString("r_name"));
            rating.add((float) obj.getDouble("ratings"));



        }









    }


    private class RestaurantSearch extends AsyncTask<Void, Void, String> {
        final ProgressDialog progressDialog = new ProgressDialog(HomePage.this,
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
                loadIntoView(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void loadIntoView(String json) throws JSONException {


        JSONArray jsonArray = new JSONArray(json);

        listOfDistances.clear();
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            if (obj.getString("name").toLowerCase().contains(searchText.getText().toString().toLowerCase())) {
                //restaurant.setText(obj.getString("name"));
                //Picasso.with(getApplicationContext()).load(obj.getString("image")).into(pic);
                count++;
                latitude1 = obj.getDouble("latitude");
                longitude1 = obj.getDouble("longitude");
                tracker = new LocationTracker(HomePage.this);
                if (tracker.canGetLocation()) {
                    latitude2 = tracker.getLatitude();
                    longitude2 = tracker.getLongitude();
                   /* double earthRadius = 6371; //meters
                    double dLat = Math.toRadians(latitude2 - latitude1);
                    double dLng = Math.toRadians(longitude2 - longitude1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                                    Math.sin(dLng / 2) * Math.sin(dLng / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    dist = (double) (earthRadius * c);*/
                    Location loc1 = new Location("");
                    loc1.setLatitude(latitude1);
                    loc1.setLongitude(longitude1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(latitude2);
                    loc2.setLongitude(longitude2);

                     dist = loc1.distanceTo(loc2);
                     dist=dist/1000;





                } else {
                    tracker.showSettingsAlert();
                }

                listOfImages.add(obj.getString("image"));
                listOfNames.add(obj.getString("name"));
                listOfRestaurantIds.add(obj.getInt("id"));
                listOfDistances.add(dist);
                listOfLatitudes.add(latitude1);
                listOfLongitudes.add(longitude1);




                if(count==0){
                    Toast.makeText(HomePage.this,"No Record Found",Toast.LENGTH_LONG).show();
                }

        }


    }




        arrayAdapter=new CustomAdapter(HomePage.this,listOfNames,listOfImages,listOfDistances,rating,r_name);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(HomePage.this,RatingandComments.class);
                intent.putExtra("restaurant_id",listOfRestaurantIds.get(i));
                intent.putExtra("user_id",userid);
                intent.putExtra("images",listOfImages.get(i));
                intent.putExtra("restaurant_name",listOfNames.get(i));
                intent.putExtra("user_email",email);
                intent.putExtra("latitude",listOfLatitudes.get(i));
                intent.putExtra("longitude",listOfLongitudes.get(i));
                startActivity(intent);
            }
        });

        arrayAdapter.notifyDataSetChanged();



    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private class loadRestaurant extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



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

            try {
                loadRestaurantList(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void loadRestaurantList(String json) throws JSONException {


        JSONArray jsonArray = new JSONArray(json);


        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);


                latitude1 = obj.getDouble("latitude");
                longitude1 = obj.getDouble("longitude");
                tracker = new LocationTracker(HomePage.this);
                if (tracker.canGetLocation()) {
                    latitude2 = tracker.getLatitude();
                    longitude2 = tracker.getLongitude();
                  /*  double earthRadius = 6371; //meters
                    double dLat = Math.toRadians(latitude2 - latitude1);
                    double dLng = Math.toRadians(longitude2 - longitude1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                                    Math.sin(dLng / 2) * Math.sin(dLng / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                     dist = (double) (earthRadius * c);*/
                    Location loc1 = new Location("");
                    loc1.setLatitude(latitude1);
                    loc1.setLongitude(longitude1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(latitude2);
                    loc2.setLongitude(longitude2);

                    dist = loc1.distanceTo(loc2)/1000;





                } else {
                    tracker.showSettingsAlert();
                }


                listOfImages.add(obj.getString("image"));
                listOfNames.add(obj.getString("name"));
                listOfRestaurantIds.add(obj.getInt("id"));
                listOfDistances.add(dist);
                listOfLatitudes.add(latitude1);
                listOfLongitudes.add(longitude1);






        }





        arrayAdapter=new CustomAdapter(HomePage.this,listOfNames,listOfImages,listOfDistances,rating,r_name);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(HomePage.this,RatingandComments.class);
                intent.putExtra("restaurant_id",listOfRestaurantIds.get(i));
                intent.putExtra("user_id",userid);
                intent.putExtra("images",listOfImages.get(i));
                intent.putExtra("restaurant_name",listOfNames.get(i));
                intent.putExtra("user_email",email);
                intent.putExtra("latitude",listOfLatitudes.get(i));
                intent.putExtra("longitude",listOfLongitudes.get(i));
                startActivity(intent);
            }
        });

        arrayAdapter.notifyDataSetChanged();



    }


}
