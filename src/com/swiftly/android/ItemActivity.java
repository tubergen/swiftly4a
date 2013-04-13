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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.swiftly.android.savagelook.UrlJsonAsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.lang.Void;import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends Activity {
    private static final String TASKS_URL = "http://10.8.179.92:3000/api/v1/items.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemActivity.this, CartActivity.class));
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        loadTasksFromAPI(TASKS_URL);
    }

    private void loadTasksFromAPI(String url) {
        GetTasksTask getTasksTask = new GetTasksTask(ItemActivity.this);
        getTasksTask.setMessageLoading("Loading tasks...");

        Log.d("ItemActivity", "execute!");
        getTasksTask.execute(url);
    }

    private class GetTasksTask extends UrlJsonAsyncTask {
        public GetTasksTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                Log.d("ItemActivity", "onPostExecute");
                JSONArray jsonTasks = json.getJSONObject("data").getJSONArray("items");
                int length = jsonTasks.length();
                List<String> tasksTitles = new ArrayList<String>(length);

                for (int i = 0; i < length; i++) {
                    tasksTitles.add(jsonTasks.getJSONObject(i).getString("title"));
                }

                ListView tasksListView = (ListView) findViewById (R.id.tasks_list_view);
                if (tasksListView != null) {
                    tasksListView.setAdapter(new ArrayAdapter<String>(ItemActivity.this,
                            android.R.layout.simple_list_item_1, tasksTitles));
                }

                new LoadImageTask().execute(json.getJSONObject("data").getString("image_url"));

            } catch (Exception e) {
                Log.d("ItemActivity", "caught exception: " + e);
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }


    public class LoadImageTask extends AsyncTask<String, Void, Void> {
        private Bitmap img;

        @Override
        protected Void doInBackground(String... urls) {
            Log.d("ItemActivity", "load image task dIB");
            // updating UI from Background Thread
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urls[0]).openConnection();
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
            ((ImageView) findViewById(R.id.product_image)).setImageBitmap(img);
            Toast.makeText(ItemActivity.this, img.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
