package com.prescryp.deliveryapp.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prescryp.deliveryapp.Misc.RunTimePermission;
import com.prescryp.deliveryapp.Model.PickupItem;
import com.prescryp.deliveryapp.R;

import java.util.List;

public class PickupListAdapter extends RecyclerView.Adapter<PickupListAdapter.ViewHolder>{

    private List<PickupItem> listItems;
    private Context context;
    private Activity activity;
    private RunTimePermission photoRunTimePermission;

    public PickupListAdapter(List<PickupItem> listItems, Context context, Activity activity){
        this.listItems = listItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_details_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PickupItem listItem = listItems.get(position);

        holder.seller_store_name_text.setText(listItem.getSellerStoreName());
        holder.pickup_address_text.setText(listItem.getSellerStoreAddress());
        holder.call_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + listItem.getSellerStoreContact()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent);
                } else {
                    photoRunTimePermission = new RunTimePermission(activity);
                    photoRunTimePermission.requestPermission(new String[]{
                            Manifest.permission.CALL_PHONE
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                context.startActivity(intent);
                            }

                        }

                        @Override
                        public void permissionDenied() {
                        }
                    });
                }

            }
        });

        holder.pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nav_uri = "google.navigation:q=" + listItem.getStoreLatitude().trim() + "," + listItem.getStoreLongitude().trim() + "&avoid=tf";
                Uri gmmIntentUri = Uri.parse(nav_uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        OrderItemListAdapter adapter = new OrderItemListAdapter(listItem.getOrderItemsList(), context);
        holder.order_item_recycler_view.setAdapter(adapter);
        holder.order_item_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        holder.order_item_recycler_view.setHasFixedSize(true);


    }




    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView seller_store_name_text, pickup_address_text;
        ConstraintLayout call_store, pickup_location;
        RecyclerView order_item_recycler_view;

        ViewHolder(View itemview){
            super(itemview);
            seller_store_name_text = itemview.findViewById(R.id.seller_store_name_text);
            pickup_address_text = itemview.findViewById(R.id.pickup_address_text);
            call_store =  itemview.findViewById(R.id.call_store);
            pickup_location = itemview.findViewById(R.id.pickup_location);
            order_item_recycler_view = itemview.findViewById(R.id.order_item_recycler_view);

        }
    }
}
