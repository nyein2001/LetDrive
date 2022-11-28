package com.lightidea.products.letdrive.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.model.CustomerDataModel;
import com.lightidea.products.letdrive.utils.Tools;

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
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class CustomerLocationActivity extends AppCompatActivity {

    private GeoPoint driverGeoPoint, customerGeoPoint;

    private MapView map;
    private CustomerDataModel customerInfo;

    private boolean no_permission_result = false;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String MY_UER_AGENT = "OBP_Tuto/1.0";
    private Context context;
    private MyLocationNewOverlay mLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        setContentView(R.layout.activity_customer_location);
        map = findViewById(R.id.map_view);
        customerInfo = (CustomerDataModel) getIntent().getSerializableExtra("INFO");

        setupCustomerLocation();
        initSetupOSMM();
        startAction();

    }

    public void setupCustomerLocation() {
        double customerLat = customerInfo.getLat();
        double customerLng = customerInfo.getLog();
        customerGeoPoint = new GeoPoint(customerLat, customerLng);
        Log.d(TAG, "Customer latitude : " + customerLat + "  Customer longitude : " + customerLng);
        setCustomerMarker(customerGeoPoint, R.drawable.ic_marker);
    }

    public void initSetupOSMM() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15);
        mapController.setCenter(customerGeoPoint);

        setupCompass();
        setupRotationGesture();
        setupScaleOverlay();
    }

    public void startAction() {
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.runOnFirstFix(() -> {
            driverGeoPoint = mLocationOverlay.getMyLocation();
            setMarker(driverGeoPoint, org.osmdroid.bonuspack.R.drawable.marker_cluster, "Your Current Location");
            new loadRoadMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        map.getOverlays().add(this.mLocationOverlay);
    }

    public void setMarker(GeoPoint geoPoint, int drawable, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), drawable, null));
        marker.setTitle(title);
    }

    public void setCustomerMarker(GeoPoint geoPoint, int drawable) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), drawable, null));
        marker.setTitle(customerInfo.getName() + "\n"
                + customerInfo.getPhone());
        new Thread(() -> {
            try {
                marker.setImage(Tools.convertDrawableFromUrl(customerInfo.getPhoto()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void setupCompass() {
        CompassOverlay mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);
    }

    public void setupRotationGesture() {
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
    }

    public void setupScaleOverlay() {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Tools.needRequestPermission() && !no_permission_result) {
            String[] permission = Tools.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && Tools.needRequestPermission()) {
            no_permission_result = true;
            Log.d(TAG, "Permission : " + permissions[0] + "was" + grantResults[0]);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class loadRoadMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RoadManager roadManager = new OSRMRoadManager(context, MY_UER_AGENT);
            ArrayList<GeoPoint> wayPoints = new ArrayList<>();
            wayPoints.add(driverGeoPoint);
            wayPoints.add(customerGeoPoint);
            Road road = roadManager.getRoad(wayPoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            map.getOverlays().add(roadOverlay);

            ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);

            Drawable nodeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker, null);
                for (int i = 0; i < road.mNodes.size(); i++) {
                    RoadNode node = road.mNodes.get(i);
                    Marker nodeMarker = new Marker(map);
                    nodeMarker.setPosition(node.mLocation);
                    nodeMarker.setIcon(nodeIcon);
                    nodeMarker.setTitle("Step " + i);
                    nodeMarker.setSnippet(node.mInstructions);
                    nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                    Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_continue, null);
                    nodeMarker.setImage(icon);
                    map.getOverlays().add(nodeMarker);
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            map.invalidate();
            super.onPostExecute(unused);
        }
    }
}