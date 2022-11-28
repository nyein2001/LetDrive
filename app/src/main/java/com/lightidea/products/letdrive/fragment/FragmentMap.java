package com.lightidea.products.letdrive.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.model.CustomerDataModel;
import com.lightidea.products.letdrive.ui.CustomerLocationActivity;
import com.lightidea.products.letdrive.utils.Tools;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


public class FragmentMap extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MapView map;
    private MyLocationNewOverlay locationOverlay;
    private GeoPoint driverGeoPoint;
    private GeoPoint geoPoint;
    private double customerLat, customerLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        map = rootView.findViewById(R.id.map_fragment);
        initSetupOSM();
        startAction();
        return rootView;
    }

    public void initSetupOSM() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        IMapController mapController = map.getController();
        GeoPoint centerPoint = new GeoPoint(16.826286386456776, 96.12532438059695);
        mapController.setCenter(centerPoint);
        mapController.setZoom(9.5);

        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()),map);
        this.locationOverlay.enableMyLocation();
        this.locationOverlay.enableFollowLocation();
        this.locationOverlay.runOnFirstFix(() -> {
            driverGeoPoint = locationOverlay.getMyLocation();
            setMarker(driverGeoPoint, org.osmdroid.bonuspack.R.drawable.marker_cluster, "Your Current Location");
            setAllLocation();
        });
        map.getOverlays().add(this.locationOverlay);
        map.invalidate();
    }

    public void startAction() {

    }

    public void setAllLocation() {
        final CollectionReference customer_info = db.collection("customer");
        customer_info.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.google.firebase.firestore.GeoPoint point = document.getGeoPoint("location");
                            customerLat = point.getLatitude();
                            customerLng = point.getLongitude();
                            geoPoint = new GeoPoint(customerLat, customerLng);
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String photo = document.getString("photo");
                            Log.d("*** Output ***", customerLat + " " + customerLng + " " + name + " " + phone + " " + photo);
                            CustomerDataModel dataModel = new CustomerDataModel(customerLat, customerLng, name, phone, photo);
                            new Thread(() -> {
                                try {
                                    setCustomerMarker(geoPoint, Tools.convertDrawableFromUrl(document.getString("photo")), name, phone);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                            goToRoadMap(dataModel);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("onFailure", e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void goToRoadMap(CustomerDataModel dataModel) {
        MapEventsReceiver mReceive = new MapEventsReceiver()
        {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p)
            {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p)
            {
                Intent intent = new Intent(requireContext(), CustomerLocationActivity.class);
                intent.putExtra("INFO", dataModel);
                requireContext().startActivity(intent);
                return false;
            }
        };
        MapEventsOverlay evOverlay = new MapEventsOverlay(mReceive);
        map.getOverlays().add(evOverlay);
    }

    public void setMarker(GeoPoint geoPoint, int drawable, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), drawable, null));
        marker.setTitle(title);
    }

    public void setCustomerMarker(GeoPoint geoPoint, Drawable drawable, String name, String phone) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(requireContext().getResources(), R.drawable.ic_marker, null));
        marker.setTitle(name + "\n" + phone);
        marker.setImage(drawable);
    }

    public void setupCompass() {
        CompassOverlay mCompassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);
    }

    public void setupRotationGesture() {
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(requireContext(), map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
    }

    public void setupScaleOverlay() {
        final DisplayMetrics dm = requireContext().getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);
    }
}
