package com.lightidea.products.letdrive.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.adapter.CustomerAdapter;
import com.lightidea.products.letdrive.model.CustomerDataModel;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentHome extends Fragment {

    private View root_view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CustomerAdapter mAdapter;
    private ArrayList<CustomerDataModel> arrayList = new ArrayList<>();
    private ArrayList<CustomerDataModel> sortingList = new ArrayList<>();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.home_fragment, container, false);
        initComponents();
        startAction();
        final CollectionReference customer_info = db.collection("customer");
        customer_info.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GeoPoint geoPoint = document.getGeoPoint("location");
                            double lat = geoPoint.getLatitude();
                            double lng = geoPoint.getLongitude();
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String photo = document.getString("photo");
                            Log.d("*** Output ***", lat + " " + lng + " " + name + " " + phone + " " + photo);
                            CustomerDataModel dataModel = new CustomerDataModel(lat,lng,name,phone,photo);
                            arrayList.add(dataModel);

                            mAdapter = new CustomerAdapter(getContext(), arrayList);
                            recyclerView.setAdapter(mAdapter);
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("onFailure", e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        return root_view;
    }

    public void initComponents() {
        progressBar = root_view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        recyclerView = root_view.findViewById(R.id.customer_recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

    public void startAction() {

    }

}
