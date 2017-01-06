package com.tromke.mydrive.Home.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tromke.mydrive.R;

/**
 * Created by Devrath on 10-09-2016.
 */
public class AdptDocNames extends BaseAdapter {
    Context context;
    TypedArray mdocImgs;
    TypedArray mDocNames;
    LayoutInflater inflter;

    public AdptDocNames(Context applicationContext, TypedArray flags, TypedArray countryNames) {
        this.context = applicationContext;
        this.mdocImgs = flags;
        this.mDocNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return mdocImgs.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        ImageView imgDoc = (ImageView) view.findViewById(R.id.imgDoc);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        imgDoc.setImageResource(mdocImgs.getResourceId(i,R.drawable.ic_launcher));
        txtName.setText(mDocNames.getResourceId(i,R.string.app_name));
        return view;
    }
}
