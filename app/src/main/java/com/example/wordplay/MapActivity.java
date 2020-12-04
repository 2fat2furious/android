package com.example.wordplay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener {

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private MapView mapView;
    private MyLocationNewOverlay oMapLocationOverlay;
    private Context context;
    private Location myLocation;
    private Location lastLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context = this;
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        checkPermissionsState();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED) {

            setupMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean somePermissionWasDenied = false;
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true;
                        }
                    }
                    if (somePermissionWasDenied) {
                        Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        setupMap();
                    }
                } else {
                    Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setupMap() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        mapView = findViewById(R.id.mapview);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        mapView.getController().setZoom(10);
        mapView.setTilesScaledToDpi(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        oMapLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mapView);
        mapView.getOverlays().add(oMapLocationOverlay);
        oMapLocationOverlay.enableFollowLocation();
        oMapLocationOverlay.enableMyLocation();

        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        TextView latLongTv = findViewById(R.id.textView);
        latLongTv.setText("" + location.getLatitude() + ", " + location.getLongitude());
        if(myLocation == null){
            myLocation = location;
        }
        if(location.getLatitude() != myLocation.getLatitude() || location.getLongitude() != myLocation.getLongitude()){
            lastLocation = myLocation;
            myLocation = location;
            if(lastLocation != null && myLocation != null && (lastLocation.getLatitude() != myLocation.getLatitude() || lastLocation.getLongitude() != myLocation.getLongitude())) {
                ArrayList<GeoPoint> geoPoints = new ArrayList<>();
                geoPoints.add(new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()));
                geoPoints.add(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                new UpdateRoadTask().execute(geoPoints);
            }
        }
    }

    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new OSRMRoadManager(context);
            return roadManager.getRoads(waypoints);
        }

        @Override
        protected void onPostExecute(Road[] roads) {
            Road[] mRoads = roads;
            if (roads == null)
                return;
            if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
                Toast.makeText(mapView.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
            else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
                Toast.makeText(mapView.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
            Polyline[] mRoadOverlays = new Polyline[roads.length];
            List<Overlay> mapOverlays = mapView.getOverlays();
            for (int i = 0; i < roads.length; i++) {
                Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i]);
                mRoadOverlays[i] = roadPolyline;
                String routeDesc = roads[i].getLengthDurationText(context, -1);
                roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
                roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView));
                roadPolyline.setRelatedObject(i);
//                roadPolyline.setOnClickListener(new RoadOnClickListener());
                mapOverlays.add(1, roadPolyline);
                //selectRoad(0);
//                map.invalidate();
                //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
                //to avoid covering the other overlays.
            }
        }
    }
}

