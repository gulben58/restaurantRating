package com.example.gulbe.restaurantratingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by gulbe on 14.04.2018.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<String> images;
    private final ArrayList<Double> distances;
    private final ArrayList<Float> ratings;
    private final ArrayList<String> r_name;




    public CustomAdapter(Activity context, ArrayList<String> name, ArrayList<String> images,ArrayList<Double> distances,ArrayList<Float> ratings,ArrayList<String> r_name) {
        super(context, R.layout.custom_list, name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.name=name;
        this.images=images;
        this.distances=distances;
        this.r_name=r_name;
        this.ratings=ratings;
    }

    public View getView(int position, View view, ViewGroup parent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list, null,true);

        TextView txtName = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        TextView dist=(TextView) rowView.findViewById(R.id.distText);
        RatingBar rtng=(RatingBar) rowView.findViewById(R.id.ratingBar);

        for(int i=0;i<r_name.size();i++){
            if(r_name.get(i).equals(name.get(position))){
                rtng.setRating((float)ratings.get(i));
            }
        }



        dist.setText(new DecimalFormat("##.#").format(distances.get(position))+"km");
        txtName.setText(name.get(position));




        String img_url=images.get(position);

        if (!img_url.equals("")){
            Picasso.with(context).load(img_url).into(imageView);
        }







        return rowView;

    }






}
