package com.swiftly.android;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CartActivity extends Activity {
    private ItemsDbAdapter mDbHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        mDbHelper = new ItemsDbAdapter(this);
        mDbHelper.open();

        populateCartView();
    }

    public void populateCartView() {
        Cursor cursor = mDbHelper.getCart();

        // The desired columns to be bound
        String[] columns = new String[] {
                ItemsDbAdapter.KEY_ITEM_NAME,
                ItemsDbAdapter.KEY_ITEM_PRICE
                //ItemsDbAdapter.KEY_ITEM_LOCAL_IMG_PATH
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.name,
                R.id.price
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                this, R.layout.cart_item,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }
}