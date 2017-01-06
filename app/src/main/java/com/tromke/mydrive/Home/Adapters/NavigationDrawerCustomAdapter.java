package com.tromke.mydrive.Home.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tromke.mydrive.Models.NavigationDrawerDataModel;
import com.tromke.mydrive.R;

/**
 * Created by drrao on 1/6/2017.
 */
public class NavigationDrawerCustomAdapter extends ArrayAdapter<NavigationDrawerDataModel> {
    Context context;
    int resourceId;
    NavigationDrawerDataModel navigationDrawerDataModel [] = null;

    public NavigationDrawerCustomAdapter(Context context, int resource, NavigationDrawerDataModel[] objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.context = context;
        navigationDrawerDataModel = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        listItem = layoutInflater.inflate(resourceId,parent,false);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        NavigationDrawerDataModel data = navigationDrawerDataModel[position];
        textViewName.setText(data.name);

        return listItem;
    }
}
