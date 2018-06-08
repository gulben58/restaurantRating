package com.example.gulbe.restaurantratingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    ImageButton profile;
    private static int RESULT_LOAD_IMAGE = 1;
    int user_id;
    String email;
    TextView usermail;
    ArrayAdapter<String> adapter;
    ListView comments;
    ArrayList<String> listOfComments;
    ArrayList<String> listOfRnames;
    Button logout;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        listOfComments=new ArrayList<>();
        listOfRnames=new ArrayList<>();
        comments=(ListView) findViewById(R.id.commentList);
        Bundle extras=getIntent().getExtras();
        user_id=extras.getInt("user_id");
        email=extras.getString("email");
        usermail=(TextView) findViewById(R.id.username);
        usermail.setText(email);
        profile=(ImageButton) findViewById(R.id.profil);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        sp = getSharedPreferences("login",MODE_PRIVATE);
        logout=(Button) findViewById(R.id.logout4);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                sp.edit().putBoolean("logged",false).apply();
                startActivity(intent);
            }
        });

        new GetCommentList().execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageButton image = (ImageButton) findViewById(R.id.profil);
            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }
    private class GetCommentList extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.128.86/profileToJSON.php");
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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (user_id == obj.getInt("user_id")) {
                listOfComments.add(obj.getString("comments"));
                listOfRnames.add(obj.getString("r_name"));
            }

        }
        adapter=new ProfileCustomAdapter(ProfileActivity.this,listOfComments,listOfRnames);
        comments.setAdapter(adapter);
    }

}
