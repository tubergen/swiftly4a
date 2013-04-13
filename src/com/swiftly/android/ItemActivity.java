package com.swiftly.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.swiftly.android.savagelook.UrlJsonAsyncTask;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.lang.Void;import java.net.HttpURLConnection;
import java.net.URL;

public class ItemActivity extends Activity {
    private static final String ITEM_URL = "http://10.8.179.92:3000/api/v1/item.json";
    private ItemsDbAdapter mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        int barcode = 12591; // barcode scanner activity should put this in an intent and launch this activity

        mDbHelper = new ItemsDbAdapter(this);
        mDbHelper.open();
        loadItemFromAPI(ITEM_URL, barcode);
    }

    private void loadItemFromAPI(String url, int barcode) {
        GetItemTask getItemTask = new GetItemTask(ItemActivity.this);
        getItemTask.setMessageLoading("Loading item...");
        getItemTask.execute(url);
    }

    private class GetItemTask extends UrlJsonAsyncTask {
        public GetItemTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                Log.d("ItemActivity", "onPostExecute");
                JSONObject i = json.getJSONArray("items").getJSONObject(0);

                final Item item = new Item(
                        i.getString("name"),
                        i.getInt("barcode"),
                        i.getInt("id"),
                        i.getString("web_img_path"),
                        "",
                        (float) i.getDouble("price"),
                        false);

                int barcode = 12591; // this should be passed in, eventually
                if (mDbHelper.getItemByBarcode(barcode) == null) {
                    mDbHelper.putItem(item);
                } else {
                    // Yes, we always update the item with data from the web.
                    mDbHelper.updateItem(item);
                }

                // Update the UI
                ((TextView) findViewById(R.id.item_name)).setText(item.name);
                ((TextView) findViewById(R.id.item_price)).setText("$" + item.price);

                View addToCart = findViewById(R.id.add_to_cart);
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.isInCart = true;
                        mDbHelper.updateItem(item);
                        startActivity(new Intent(ItemActivity.this, CartActivity.class));
                    }
                });
                addToCart.setVisibility(View.VISIBLE);

                // Load the item's image
                new LoadImageTask().execute(item);
            } catch (Exception e) {
                Log.d("ItemActivity", "caught exception: " + e);
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }


    public class LoadImageTask extends AsyncTask<Item, Void, Void> {
        private Bitmap img;
        private Item item;

        @Override
        protected Void doInBackground(Item... items) {
            Log.d("ItemActivity", "load image task dIB");
            // updating UI from Background Thread
            try {
                item = items[0];
                HttpURLConnection conn = (HttpURLConnection) new URL(item.webImgPath).openConnection();
                conn.setDoInput(true);
                conn.connect();
                img = BitmapFactory.decodeStream(conn.getInputStream());
            }
            catch (IOException e) {
                Log.d("ItemActivity", "caught exception: " + e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d("ItemActivity", "load image task oPE");
            ((ImageView) findViewById(R.id.item_img)).setImageBitmap(img);
            Toast.makeText(ItemActivity.this, img.toString(), Toast.LENGTH_LONG).show();

            // cache the image
            File f = new File(getCacheDir(), "" + item.barcode);
            try {
                FileOutputStream out = new FileOutputStream(f);
                img.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                item.localImgPath = f.getPath();
                mDbHelper.updateItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
