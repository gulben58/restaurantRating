package com.example.gulbe.restaurantratingapp;

import android.app.Activity;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gulbe on 25.05.2018.
 */

public class ProfileCustomAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> comments;
    private final ArrayList<String> restaurants;
    private final Activity context;
    TextView user;
    TextView commnt;

    public ProfileCustomAdapter(Activity context, ArrayList<String> comments, ArrayList<String> restaurants) {
        super(context, R.layout.profilecustomlist, comments);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.comments=comments;
        this.restaurants=restaurants;
    }

    public View getView(int position, View view, ViewGroup parent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.profilecustomlist, null,true);
        user=(TextView) rowView.findViewById(R.id.restaurant);
        commnt=(TextView) rowView.findViewById(R.id.userCom);

        user.setText(restaurants.get(position));
        commnt.setText(comments.get(position));








        return rowView;

    }







}


