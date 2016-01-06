package com.chinaway.android.trucktracker;

import android.chinaway.com.trucktracker.R;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinaway.android.library.locationtracker.LocationTracker;


public class MainActivity extends ActionBarActivity {

    private TextView mLocationOutput;
    private boolean mBeepFlag = false;
    private int mLocationCount = 0;
    private LocationTracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationOutput = (TextView) findViewById(R.id.output);

        final Button btnStart = (Button) findViewById(R.id.btn_start);
        final Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        mTracker = new LocationTracker.Builder(this)
//                        .setMode(LocationTracker.GPS_MODE)
                        .setLocationListener(new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                String info = "location: " + location.getLongitude() + ", " + location.getLatitude()
                                        + "\nprovider: " + location.getProvider()
                                        + "\naccuracy: " + location.getAccuracy() + "\n" + (++mLocationCount);
                                if (mBeepFlag) {
                                    mLocationOutput.setTextColor(Color.RED);
                                } else {
                                    mLocationOutput.setTextColor(Color.BLUE);
                                }
                                mBeepFlag = !mBeepFlag;
                                mLocationOutput.setText(info);
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        }).build();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
                mTracker.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                mTracker.stop();
                mLocationOutput.setText("hello, world");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTracker.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
