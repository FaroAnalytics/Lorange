package com.faroanalytics.lorange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;



public class ClassListAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public ClassListAdapter(Context ctx, int resource) {super(ctx, resource);}

    public void add(Alumni object) {
        list.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater shopInflater = LayoutInflater.from(getContext());
        View customView = shopInflater.inflate(R.layout.class_mate_layout, parent, false);

        Alumni exceptional = (Alumni) getItem(position);
        TextView mateName = (TextView) customView.findViewById(R.id.tvName);
        TextView mateInfo = (TextView) customView.findViewById(R.id.tvInfo);
        ImageView ShopImage = (ImageView) customView.findViewById(R.id.ivPicture);

        mateName.setText(exceptional.getName());
        mateInfo.setText(exceptional.getJob() + ", " + exceptional.getResidence());

        DownloadImage downloadImage = new DownloadImage(ShopImage);
        downloadImage.execute(exceptional.getPicture());

        return customView;
    }
}

