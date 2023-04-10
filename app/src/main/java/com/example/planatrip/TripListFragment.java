package com.example.planatrip;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TripListFragment extends Fragment {

    private MyDatabaseHelper mDbHelper;
    private TestMapFragment mapFragment = new TestMapFragment();



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.triplist_fragment, container, false);
        mDbHelper = new MyDatabaseHelper(getActivity());
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MyAdapter adapter = new MyAdapter(getContext(), getDataFromDatabase());
        recyclerView.setAdapter(adapter);

/*        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyObject myObject) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                // Handle the selected item
                String stringas = myObject.getNameOfTrip();
                ContentValues contentValues = new ContentValues();
                contentValues.put("currenttrip_string", stringas);
                //Toast.makeText(getActivity(), "Selected item: " + myObject.getNameOfTrip(), Toast.LENGTH_SHORT).show();
                db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});

            }
        });*/

        adapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(MyObject myObject, View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_select:
                                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                // Handle the selected item
                                String stringas = myObject.getNameOfTrip();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("currenttrip_string", stringas);
                                //Toast.makeText(getActivity(), "Selected item: " + myObject.getNameOfTrip(), Toast.LENGTH_SHORT).show();
                                db.update("CURRENT_TRIP",contentValues,"_id=?",new String[]{"0"});
                                return true;
                            case R.id.action_delete:
                                String stringas2 = myObject.getNameOfTrip();
                                SQLiteDatabase db2 = mDbHelper.getWritableDatabase();
                                db2.execSQL("DELETE FROM " + MyDatabaseHelper.TABLE_NAME + " WHERE " + MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING + "='" + stringas2 + "'");
                                db2.close();
                                return true;
                            case R.id.action_edit:
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }


    private List<MyObject> getDataFromDatabase() {
        List<MyObject> myObjects = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING
        };

        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            // Get data from cursor and add to myObjects list
            String nameOfTrip = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_NAMEOFTRIP_STRING));
            MyObject myObject = new MyObject(nameOfTrip);
            myObjects.add(myObject);
        }

        cursor.close();
        db.close();
        return myObjects;
    }

}
