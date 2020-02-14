package com.example.richa_764947_androidlab;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FavPlaceAdapter extends ArrayAdapter {
   Context mContext;
   int layoutRes;
   List<FavouritePlace> favouritePlaces;
   DatabaseHelper mdatabase;

    public FavPlaceAdapter(@NonNull  Context mContext, int layoutRes, List<FavouritePlace> favouritePlaces, DatabaseHelper mdatabase) {
        super(mContext, layoutRes, favouritePlaces);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.favouritePlaces = favouritePlaces;
        this.mdatabase = mdatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater =LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutRes,null);

        TextView name = view.findViewById(R.id.tv_placename);
        TextView address = view.findViewById(R.id.tv_desc);
        TextView date = view.findViewById(R.id.tv_date);

        final FavouritePlace place = favouritePlaces.get(position);
        name.setText(place.getPlaceName());
        address.setText(place.getAddress());
        date.setText(place.getDate());


        return view;

    }


    private void loadPlaces() {
        Cursor cursor = mdatabase.getAllPlace();
        if (cursor.moveToFirst()) {

            do {
                favouritePlaces.add(new FavouritePlace(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5)));


            } while (cursor.moveToNext());

            cursor.close();

        }



    }

    public void setNotifyOnChange() {
        //favouritePlaces = null;
        loadPlaces();
    }
}
