/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swiftly.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.holoeverywhere.app.Activity;

public class BaseActivity extends Activity {
    public static int THEME = R.style.Holo_Theme;
    protected int mCartItemId = 0;
    protected int mScanItemId = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = THEME == R.style.Holo_Theme_Light;

        menu.add(Menu.NONE, mCartItemId, Menu.NONE, "Cart").setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(BaseActivity.this, CartActivity.class));
                        return true;
                    }
                })
                .setIcon(isLight ? R.drawable.shop_cart_inverse : R.drawable.shop_cart)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, mScanItemId, Menu.NONE, "Scan").setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(BaseActivity.this, ScanActivity.class));
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);


        // Customize the action bar's font and remove the icon
        TextView title = (TextView) findViewById(com.actionbarsherlock.R.id.abs__action_bar_title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        title.setTypeface(((MyApplication) getApplication()).getCustomTypeface());
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        setContent((TextView)findViewById(R.id.text));
    }

    protected void setContent(TextView view) {
        view.setText(R.string.action_items_content);
    }
}
