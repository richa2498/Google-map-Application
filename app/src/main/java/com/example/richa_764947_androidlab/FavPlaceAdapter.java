package com.example.richa_764947_androidlab;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class FavPlaceAdapter extends ArrayAdapter {
   Context mContext;
   int layoutRes;
   List<FavouritePlace> favouritePlaces;
   DatabaseHelper mdatabase;

    public FavPlaceAdapter(@NonNull Context context, int resource, @NonNull List objects, Context mContext, int layoutRes, List<FavouritePlace> favouritePlaces, DatabaseHelper mdatabase) {
        super(context, resource, objects);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.favouritePlaces = favouritePlaces;
        this.mdatabase = mdatabase;
    }




}
