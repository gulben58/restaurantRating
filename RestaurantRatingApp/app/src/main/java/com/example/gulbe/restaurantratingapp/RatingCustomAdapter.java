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

public class RatingCustomAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> comments;
    private final ArrayList<String> emails;
    private final Activity context;
    TextView user;
    TextView commnt;

    public RatingCustomAdapter(Activity context, ArrayList<String> comments, ArrayList<String> emails) {
        super(context, R.layout.commentlist, comments);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.comments=comments;
        this.emails=emails;
    }

    public View getView(int position, View view, ViewGroup parent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.commentlist, null,true);
        user=(TextView) rowView.findViewById(R.id.user);
        commnt=(TextView) rowView.findViewById(R.id.com);

        user.setText(emails.get(position));
        commnt.setText(comments.get(position));








        return rowView;

    }







}


