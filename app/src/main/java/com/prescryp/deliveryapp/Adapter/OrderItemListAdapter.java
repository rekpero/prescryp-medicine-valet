package com.prescryp.deliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prescryp.deliveryapp.Model.OrderItem;
import com.prescryp.deliveryapp.R;

import java.util.List;

public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemListAdapter.ViewHolder> {

    private List<OrderItem> listItems;
    private Context context;

    public OrderItemListAdapter(List<OrderItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final OrderItem listItem = listItems.get(position);

        holder.medicineName.setText(listItem.getMedicineName());
        holder.quantity.setText(listItem.getQuantity());
        holder.package_contain.setText(listItem.getPackageContain());
        if (listItem.getItemStatus().equalsIgnoreCase("Rejected")){
            holder.confirmed_order.setVisibility(View.GONE);
            holder.rejected_order.setVisibility(View.VISIBLE);
        }else if (listItem.getItemStatus().equalsIgnoreCase("Confirmed")){
            holder.rejected_order.setVisibility(View.GONE);
            holder.confirmed_order.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medicineName, package_contain, quantity;
        public ConstraintLayout confirmed_order, rejected_order;


        public ViewHolder(View itemview){
            super(itemview);
            medicineName = itemview.findViewById(R.id.medicine_name_order);
            package_contain = itemview.findViewById(R.id.package_contain);
            quantity = itemview.findViewById(R.id.quantity);
            confirmed_order = itemview.findViewById(R.id.confirmed_order);
            rejected_order = itemview.findViewById(R.id.rejected_order);
        }
    }
}
