package com.lightidea.products.letdrive.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lightidea.products.letdrive.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MapTestActivity extends AppCompatActivity {

    private MapView mapView;
    private GeoPoint startPoint,testPoint;
    private RoadManager roadManager;
    private Road road;
    private MyLocationNewOverlay locationOverlay;
    double lat,log;

    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map_test);

        if (Build.VERSION.SDK_INT >= 23) {
            isStoragePermissionGranted();
        }

        mapView = findViewById(R.id.map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15);
//        startPoint = new GeoPoint(16.845966152726454, 96.12532889481471);


        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider,mapView);
        locationOverlay.enableFollowLocation();
        locationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
//                lat = locationOverlay.getLastFix().getLatitude();
//                log = locationOverlay.getLastFix().getLongitude();
                startPoint = locationOverlay.getMyLocation();
                mapController.setCenter(startPoint);
                setMarker(startPoint,R.drawable.ic_my_location,"Current Location");
                new loadRoadMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        mapView.getOverlayManager().add(locationOverlay);
    }

    public void setMarker(GeoPoint geoPoint,int drawable, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable( getResources(),drawable,null));
        marker.setTitle(title);
    }

    class loadRoadMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            roadManager = new OSRMRoadManager(getApplicationContext(), "OBP_Tuto/1.0");
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(startPoint);
            GeoPoint endPoint = new GeoPoint(16.847313681535077, 96.2198369558441);
            waypoints.add(endPoint);

            setMarker(endPoint,R.drawable.ic_marker,"End Point");

            road = roadManager.getRoad(waypoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            mapView.getOverlays().add(roadOverlay);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);
            Drawable nodeIcon = ResourcesCompat.getDrawable(getApplicationContext().getResources(),R.drawable.ic_marker,null);
            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(mapView);
                nodeMarker.setPosition(node.mLocation);
                nodeMarker.setIcon(nodeIcon);
                nodeMarker.setTitle("Step "+i);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(getApplicationContext(), node.mLength, node.mDuration));
                Drawable icon = ResourcesCompat.getDrawable(getApplicationContext().getResources(),R.drawable.ic_continue,null);
                nodeMarker.setImage(icon);
                mapView.getOverlays().add(nodeMarker);
            }
            mapView.invalidate();
            super.onPostExecute(unused);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "Permission is granted");
                return;
            } {
                Log.d(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "Permission is granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission : " + permissions[0] + "was" + grantResults[0]);
        }
    }
}