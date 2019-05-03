package com.safezone.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<Integer> train_images=new ArrayList<>();
    public ImageAdapter(Context c, ArrayList<Integer> images) {
        mContext = c;
        train_images=images;
    }

    @Override
    public int getCount() {
        return train_images.size();
    }

    @Override
    public Object getItem(int position) {
        return train_images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // create a new ImageView for each item referenced by the Adapter
        ImageView imageView =  new ImageView(mContext);
        imageView.setImageResource(train_images.get(position));
        imageView.setFocusable(false);
        imageView.setClickable(false);
        return imageView;
    }
}
