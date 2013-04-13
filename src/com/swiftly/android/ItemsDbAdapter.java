/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.swiftly.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.Override;import java.lang.String;

public class ItemsDbAdapter {

    public static final String KEY_ROWID = "_id";

    public static final String KEY_ITEM_NAME = "item_name";  // name of item
    public static final String KEY_ITEM_BARCODE = "item_barcode"; // barcode
    public static final String KEY_ITEM_WID = "item_wid"; // id on web
    public static final String KEY_ITEM_WEB_IMG_PATH = "item_web_img_path"; // path to image on web
    public static final String KEY_ITEM_LOCAL_IMG_PATH = "item_local_img_path"; // path to image on local storage
    public static final String KEY_ITEM_PRICE = "item_price"; // price of item
    public static final String KEY_ITEM_IS_IN_CART = "item_is_in_cart"; // is the item in the user's cart?

    private static final String TAG = "ItemsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table items (" +
                KEY_ROWID + " integer primary key autoincrement," +
                KEY_ITEM_NAME + " text not null," +
                KEY_ITEM_BARCODE + " integer not null," +
                KEY_ITEM_WID + " integer not null unique," +
                KEY_ITEM_WEB_IMG_PATH + " text not null," +
                KEY_ITEM_LOCAL_IMG_PATH + " text not null," +
                KEY_ITEM_PRICE + " float not null," +
                KEY_ITEM_IS_IN_CART + " boolean not null" +
                ");";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "items";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ItemsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the items database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ItemsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        //mDbHelper.onUpgrade(mDb, 0, 1);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    private ContentValues getCVForItem(Item i) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ITEM_NAME, i.name);
        cv.put(KEY_ITEM_BARCODE, i.barcode);
        cv.put(KEY_ITEM_WID, i.wid);
        cv.put(KEY_ITEM_WEB_IMG_PATH, i.webImgPath);
        cv.put(KEY_ITEM_LOCAL_IMG_PATH, i.localImgPath);
        cv.put(KEY_ITEM_PRICE, i.price);
        cv.put(KEY_ITEM_IS_IN_CART, i.isInCart);
        return cv;
    }

    public boolean updateItem(Item i) {
        return mDb.update(DATABASE_TABLE, getCVForItem(i), KEY_ITEM_BARCODE + "=" + i.barcode, null) > 0;
    }

    public long putItem(Item i) {
        return mDb.insert(DATABASE_TABLE, null, getCVForItem(i));
    }

    public boolean deleteItem(Item i) {
        return mDb.delete(DATABASE_TABLE, KEY_ITEM_BARCODE + "=" + i.barcode, null) > 0;
    }

    private Item getItemFromCursor(Cursor c) {
        return new Item(
                c.getString(c.getColumnIndex(KEY_ITEM_NAME)),
                c.getInt(c.getColumnIndex(KEY_ITEM_BARCODE)),
                c.getInt(c.getColumnIndex(KEY_ITEM_WID)),
                c.getString(c.getColumnIndex(KEY_ITEM_WEB_IMG_PATH)),
                c.getString(c.getColumnIndex(KEY_ITEM_LOCAL_IMG_PATH)),
                c.getFloat(c.getColumnIndex(KEY_ITEM_PRICE)),
                c.getInt(c.getColumnIndex(KEY_ITEM_IS_IN_CART)) == 1 ? true : false
        );
    }

    private String[] getKeyArray() {
        return new String[] {
                KEY_ROWID, KEY_ITEM_NAME, KEY_ITEM_BARCODE, KEY_ITEM_WID, KEY_ITEM_WEB_IMG_PATH,
                KEY_ITEM_LOCAL_IMG_PATH, KEY_ITEM_PRICE, KEY_ITEM_IS_IN_CART
        };
    }

    public Item getItemByBarcode(int barcode) {
        Cursor c = mDb.query(DATABASE_TABLE, getKeyArray(),
                KEY_ITEM_BARCODE + "=" + barcode, null, null, null, null);

        while (c.moveToNext()) {
            return getItemFromCursor(c);
        }
        return null;
    }

    public Cursor getCart() {
        return mDb.query(DATABASE_TABLE, getKeyArray(),
                KEY_ITEM_IS_IN_CART + "=" + 1, null, null, null, null);
    }
}