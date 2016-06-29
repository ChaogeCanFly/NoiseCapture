/*
 * This file is part of the NoiseCapture application and OnoMap system.
 *
 * The 'OnoMaP' system is led by Lab-STICC and Ifsttar and generates noise maps via
 * citizen-contributed noise data.
 *
 * This application is co-funded by the ENERGIC-OD Project (European Network for
 * Redistributing Geospatial Information to user Communities - Open Data). ENERGIC-OD
 * (http://www.energic-od.eu/) is partially funded under the ICT Policy Support Programme (ICT
 * PSP) as part of the Competitiveness and Innovation Framework Programme by the European
 * Community. The application work is also supported by the French geographic portal GEOPAL of the
 * Pays de la Loire region (http://www.geopal.org).
 *
 * Copyright (C) IFSTTAR - LAE and Lab-STICC – CNRS UMR 6285 Equipe DECIDE Vannes
 *
 * NoiseCapture is a free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of
 * the License, or(at your option) any later version. NoiseCapture is distributed in the hope that
 * it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation,Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301  USA or see For more information,  write to Ifsttar,
 * 14-20 Boulevard Newton Cite Descartes, Champs sur Marne F-77447 Marne la Vallee Cedex 2 FRANCE
 *  or write to scientific.computing@ifsttar.fr
 */

package org.noise_planet.noisecapture;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    // Color for noise exposition representation
    public int[] NE_COLORS;
    protected static final Logger MAINLOGGER = LoggerFactory.getLogger(MainActivity.class);

    // For the list view
    public ListView mDrawerList;
    public DrawerLayout mDrawerLayout;
    public String[] mMenuLeft;
    public ActionBarDrawerToggle mDrawerToggle;

    public static final int PERMISSION_RECORD_AUDIO_AND_GPS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        NE_COLORS = new int[]{res.getColor(R.color.R1_SL_level),
                res.getColor(R.color.R2_SL_level),
                res.getColor(R.color.R3_SL_level),
                res.getColor(R.color.R4_SL_level),
                res.getColor(R.color.R5_SL_level)};
    }

    /**
     * If necessary request user to acquire permisions for critical ressources (gps and microphone)
     * @return True if service can be bind immediately. Otherwise the bind should be done using the
     * @see #onRequestPermissionsResult
     */
    protected boolean checkAndAskPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.RECORD_AUDIO)) {
                // After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this,
                        R.string.permission_explain_audio_record, Toast.LENGTH_LONG).show();
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this,
                        R.string.permission_explain_gps, Toast.LENGTH_LONG).show();
            }
            // Request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_RECORD_AUDIO_AND_GPS);
            return false;
        }
        return true;
    }

    void initDrawer(Integer recordId) {
        try {
            // List view
            mMenuLeft = getResources().getStringArray(R.array.dm_list_array);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.drawer_list_item, mMenuLeft));
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener(recordId));
            // Display the List view into the action bar
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {
                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(getTitle());
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle(getString(R.string.title_menu));
                }
            };
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            e.printStackTrace();
            e.printStackTrace();
        }
    }
    // Drawer navigation
    void initDrawer() {
        initDrawer(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!(this instanceof MeasurementActivity)) {
            if(mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
            Intent im = new Intent(getApplicationContext(),MeasurementActivity.class);
            im.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(im);
            finish();
        } else {
            finish();
            // Show home
            Intent im = new Intent(Intent.ACTION_MAIN);
            im.addCategory(Intent.CATEGORY_HOME);
            im.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(im);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        Integer recordId;

        public DrawerItemClickListener(Integer recordId) {
            this.recordId = recordId;
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch(position) {
                case 0:
                    // Measurement
                    Intent im = new Intent(getApplicationContext(),MeasurementActivity.class);
                    im.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(im);
                    finish();
                    break;
                case 1:
                    // Comment
                    Intent ir = new Intent(getApplicationContext(), CommentActivity.class);
                    if(recordId != null) {
                        ir.putExtra(CommentActivity.COMMENT_RECORD_ID, recordId);
                    }
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(ir);
                    finish();
                    break;
                case 2:
                    // Results
                    ir = new Intent(getApplicationContext(), Results.class);
                    if(recordId != null) {
                        ir.putExtra(Results.RESULTS_RECORD_ID, recordId);
                    }
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(ir);
                    finish();
                    break;
                case 3:
                    // History
                    Intent a = new Intent(getApplicationContext(), History.class);
                    startActivity(a);
                    finish();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    break;
                case 4:
                    // Show the map
                    Intent imap = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(imap);
                    finish();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    break;
                case 5:
                    Intent ics = new Intent(getApplicationContext(), CalibrationActivity.class);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(ics);
                    finish();
                    break;
                case 6:
                    Intent ih = new Intent(getApplicationContext(),View_html_page.class);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    ih.putExtra("pagetosee",
                            getString(R.string.url_help));
                    ih.putExtra("titletosee",
                            getString(R.string.title_activity_help));
                    startActivity(ih);
                    finish();
                    break;
                case 7:
                    Intent ia = new Intent(getApplicationContext(),View_html_page.class);
                    ia.putExtra("pagetosee",
                            getString(R.string.url_about));
                    ia.putExtra("titletosee",
                            getString(R.string.title_activity_about));
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(ia);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle = title;
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent is = new Intent(getApplicationContext(),Settings.class);
                startActivity(is);
            return true;
            /*
            case R.id.action_about:
                Intent ia = new Intent(getApplicationContext(),View_html_page.class);
                pagetosee=getString(R.string.url_about);
                titletosee=getString((R.string.title_activity_about));
                startActivity(ia);
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    protected boolean IsManualTransferOnly() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return !sharedPref.getBoolean("settings_data_transfer", true);
    }


    protected boolean IsWifiTransferOnly() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return !sharedPref.getBoolean("settings_data_transfer_wifi_only", true);
    }

    public static final class SendZipToServer implements Runnable {
        private Activity activity;
        private int recordId;
        private ProgressDialog progress;
        private final OnUploadedListener listener;

        public SendZipToServer(Activity activity, int recordId, ProgressDialog progress, OnUploadedListener listener) {
            this.activity = activity;
            this.recordId = recordId;
            this.progress = progress;
            this.listener = listener;
        }

        @Override
        public void run() {
            MeasurementUploadWPS measurementUploadWPS = new MeasurementUploadWPS(activity);
            try {
                measurementUploadWPS.uploadRecord(recordId);
                if(listener != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onMeasurementUploaded();
                        }
                    });
                }
            } catch (final IOException ex) {
                MAINLOGGER.error(ex.getLocalizedMessage(), ex);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,
                                ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } finally {
                progress.dismiss();
            }
        }
    }

    public interface OnUploadedListener {
        void onMeasurementUploaded();
    }
    // Choose color category in function of sound level
    public static int getNEcatColors(double SL) {

        int NbNEcat;

        if (SL > 75.) {
            NbNEcat = 0;
        } else if ((SL <= 75) & (SL > 65)) {
            NbNEcat = 1;
        } else if ((SL <= 65) & (SL > 55)) {
            NbNEcat = 2;
        } else if ((SL <= 55) & (SL > 45)) {
            NbNEcat = 3;
        } else {
            NbNEcat = 4;
        }
        return NbNEcat;
    }


    public static double getDouble(SharedPreferences sharedPref, String key, double defaultValue) {
        try {
            return Double.valueOf(sharedPref.getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static int getInteger(SharedPreferences sharedPref, String key, int defaultValue) {
        try {
            return Integer.valueOf(sharedPref.getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}