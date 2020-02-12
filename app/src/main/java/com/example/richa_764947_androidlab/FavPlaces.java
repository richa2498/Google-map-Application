package com.example.richa_764947_androidlab;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class FavPlaces extends AppCompatActivity {

    DatabaseHelper mDatabase;
    ArrayList<String> list ;
    List<FavouritePlace> places;
    SwipeMenuListView swipeMenuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_places);
         swipeMenuListView = (SwipeMenuListView) findViewById(R.id.favPlaceList);
         mDatabase = new DatabaseHelper(this);
         places = new ArrayList<>();
         loadPlaces();
//
//        list = new ArrayList<>();
//        list.add("richa1");
//        list.add("richa2");
//        list.add("richa3");
//        list.add("richa4");
//        list.add("richa5");
//        list.add("richa6");
//        list.add("richa7");
//        list.add("richa8");
//        list.add("richa9");
//        list.add("richa10");
//        list.add("richa11");
//        System.out.println(list.toString());

      //  ArrayAdapter adapter = new ArrayAdapter(FavPlaces.this,android.R.layout.simple_list_item_1,list);
      //  swipeMenuListView.setAdapter(adapter);

        FavPlaceAdapter adapter = new FavPlaceAdapter(this,R.layout.fav_place_cell_layout,places,mDatabase);
        swipeMenuListView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_action_name);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        swipeMenuListView.setMenuCreator(creator);

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Toast.makeText(FavPlaces.this, "case 0", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        // delete
                        Toast.makeText(FavPlaces.this, "case 1", Toast.LENGTH_SHORT).show();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


    }

    private void loadPlaces() {
        Cursor cursor = mDatabase.getAllPlace();
        if (cursor.moveToFirst()){

            do {
                places.add(new FavouritePlace(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5)));


            }while (cursor.moveToNext());

            cursor.close();
        }

    }

}
