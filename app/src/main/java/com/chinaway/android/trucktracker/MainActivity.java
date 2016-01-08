package com.chinaway.android.trucktracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import com.chinaway.android.library.locationtracker.LocationTracker;
import com.chinaway.android.library.locationtracker.sampler.LeastSquareSampler;
import com.chinaway.android.library.locationtracker.sampler.LocationSampling;
import com.chinaway.android.library.locationtracker.sampler.RandomSampler;
import com.chinaway.android.library.locationtracker.sampler.SampleLocation;
import com.chinaway.android.trucktracker.utils.LocationTransformer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String FILE_RANDOM = "random_sample";
    private static final String FILE_LEAST_SQUARE = "least_square";

    private TextView mLocationOutput;
//    private boolean mBeepFlag = false;
//    private int mLocationCount = 0;
    private LocationTracker mTracker;

    private MapView mMapView;
    private BaiduMap mMap;
    private Polyline mOldTrackLine, mNewTrackLine;


    private List<LatLng> mOldLocationList = new ArrayList<>();
    private List<LatLng> mNewLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationOutput = (TextView) findViewById(R.id.output);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();

        final Button btnStart = (Button) findViewById(R.id.btn_start);
        final Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        // 设定地图状态（设定初始中心点和缩放级数）
        LatLng szjm = new LatLng(30.498112, 104.07966);
        MapStatus mMapStatus = new MapStatus.Builder().target(szjm).zoom(15).build();

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        // 设置地图状态
        mMap.setMapStatus(mMapStatusUpdate);

        mTracker = new LocationTracker.Builder(this)
                        .setMode(LocationTracker.AUTO_MODE)
//                        .setLocationListener(new LocationListener() {
//                            @Override
//                            public void onLocationChanged(Location location) {
//                                String info = "location: " + location.getLongitude() + ", " + location.getLatitude()
//                                        + "\nprovider: " + location.getProvider()
//                                        + "\naccuracy: " + location.getAccuracy() + "\n" + (++mLocationCount);
//                                if (mBeepFlag) {
//                                    mLocationOutput.setTextColor(Color.RED);
//                                } else {
//                                    mLocationOutput.setTextColor(Color.BLUE);
//                                }
//                                mBeepFlag = !mBeepFlag;
//                                mLocationOutput.setText(info);
//                            }
//
//                            @Override
//                            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                            }
//
//                            @Override
//                            public void onProviderEnabled(String provider) {
//
//                            }
//
//                            @Override
//                            public void onProviderDisabled(String provider) {
//
//                            }
//                        })
                        .build();

//        mTracker.addSampler(new RandomSampler(new LocationSampling.LocationSamplingCallback() {
//            @Override
//            public void onNewSample(SampleLocation location) {
//                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();
//
//                double[] bdResult = new double[2];
//                LocationTransformer.transform2bd(location.latitude, location.longitude, bdResult);
//                LatLng bdLocation = new LatLng(bdResult[0], bdResult[1]);
//                mOldLocationList.add(bdLocation);
//
//                if (mOldLocationList.size() >= 2) {
//                    mMap.clear();
//                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
//                            .color(0xAAFF0000).points(mOldLocationList).visible(true);
//                    Overlay line = mMap.addOverlay(ooPolyline);
//                }
//            }
//        }, 10000));

        mTracker.addSampler(new LeastSquareSampler(new LocationSampling.LocationSamplingCallback() {
            @Override
            public void onNewSample(SampleLocation location) {
                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();

                double[] bdResult = new double[2];
                LocationTransformer.transform2bd(location.latitude, location.longitude, bdResult);
                LatLng bdLocation = new LatLng(bdResult[0], bdResult[1]);
                mNewLocationList.add(bdLocation);

                if (mNewLocationList.size() >= 2) {
                    mMap.clear();
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF78FF).points(mNewLocationList).visible(true);
                    mMap.addOverlay(ooPolyline);
                }
            }
        }, 10000));

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
                mOldLocationList.clear();
                mNewLocationList.clear();
                mMap.clear();
                mLocationOutput.setText("info");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
