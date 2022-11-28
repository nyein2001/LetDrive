package com.lightidea.products.letdrive.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.adapter.CustomerAdapter;
import com.lightidea.products.letdrive.model.CustomerDataModel;
import com.lightidea.products.letdrive.utils.Tools;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    private View root_view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText edtSearch;

    private CustomerAdapter mAdapter;
    public ArrayList<CustomerDataModel> arrayList = new ArrayList<>();
    public ArrayList<CustomerDataModel> searchingList = new ArrayList<>();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    double lat, lng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.home_fragment, container, false);
        Tools.hideKeyboard(getActivity());
        initComponents();
        searching();
        final CollectionReference customer_info = db.collection("customer");
        customer_info.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GeoPoint geoPoint = document.getGeoPoint("location");
                            lat = geoPoint.getLatitude();
                            lng = geoPoint.getLongitude();
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String photo = document.getString("photo");
                            Log.d("*** Output ***", lat + " " + lng + " " + name + " " + phone + " " + photo);
                            CustomerDataModel dataModel = new CustomerDataModel(lat, lng, name, phone, photo);
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
        edtSearch = root_view.findViewById(R.id.et_search);
    }


    public void searching() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = edtSearch.getText().toString();
                searchTerm(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void searchTerm(String s) {
        int txtLength = s.length();
        searchingList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if (txtLength <= arrayList.get(i).getName().length()) {
                if (arrayList.get(i).getName().trim().contains(s.trim())) {
                    searchingList.add(arrayList.get(i));
                }
            }
        }
        mAdapter = new CustomerAdapter(getContext(), searchingList);
        recyclerView.setAdapter(mAdapter);
    }
}
