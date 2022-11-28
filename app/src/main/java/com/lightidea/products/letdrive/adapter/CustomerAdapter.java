package com.lightidea.products.letdrive.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.model.CustomerDataModel;
import com.lightidea.products.letdrive.ui.CustomerLocationActivity;
import com.lightidea.products.letdrive.utils.Tools;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<CustomerDataModel> arrayList;

    public CustomerAdapter(Context context, ArrayList<CustomerDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerDataModel model = arrayList.get(position);
        holder.txtName.setText(model.getName());
        Tools.displayImageCircle(context,holder.customerPhoto,model.getPhoto());
        holder.txtPhone.setText(model.getPhone());
        holder.txtPhone.setMovementMethod(LinkMovementMethod.getInstance());
        holder.checkingCustomerLocation.setOnClickListener(view -> {
            Intent mapIntent = new Intent(context, CustomerLocationActivity.class);
            mapIntent.putExtra("INFO", model);
            context.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private ImageView customerPhoto;
        private TextView txtPhone;
        private View checkingCustomerLocation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
            customerPhoto = itemView.findViewById(R.id.customer_photo);
            txtPhone = itemView.findViewById(R.id.phone);
            checkingCustomerLocation = itemView.findViewById(R.id.checking_customer_location);
        }
    }
}
