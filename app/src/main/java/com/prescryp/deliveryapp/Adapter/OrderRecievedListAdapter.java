package com.prescryp.deliveryapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prescryp.deliveryapp.Model.OrderReceivedItem;
import com.prescryp.deliveryapp.OrderDetailsActivity;
import com.prescryp.deliveryapp.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderRecievedListAdapter extends RecyclerView.Adapter<OrderRecievedListAdapter.ViewHolder>{

    private List<OrderReceivedItem> listItems;
    private Context context;
    private ProgressDialog mDialog;

    public OrderRecievedListAdapter(List<OrderReceivedItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_recieved_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final OrderReceivedItem listItem = listItems.get(position);

        String date_change_format = getDateFormatChange(listItem.getDateOfOrder());
        String[] time_list =  listItem.getTimeOfOrder().split(" ");
        String time_changed = time_list[0] + " " + time_list[1].toUpperCase();

        String date_shown = date_change_format.trim() + ", " + time_changed.trim();
        holder.date_of_order.setText(date_shown);
        holder.order_number.setText(listItem.getOrderNumber());
        Locale locale = new Locale("hi", "IN");
        final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        float earning = Float.valueOf(listItem.getGrandTotal());
        holder.grand_total.setText(nf.format(Float.valueOf(earning)));

        holder.order_status.setText(listItem.getStatus());

        String order_item_shown = listItem.getOrderItems().get(0);
        for (String orderItem : listItem.getOrderItems()){
            if (!orderItem.equalsIgnoreCase(order_item_shown)){
                order_item_shown = order_item_shown + ", " + orderItem;
            }
        }
        holder.order_items.setText(order_item_shown.trim());

        holder.orderReceivedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("Order_Number", listItem.getOrderNumber());
                context.startActivity(intent);

            }
        });




    }



    private String getDateFormatChange(String date){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String outputDateStr = "";
        try {
            Date new_date = inputFormat.parse(date);
            outputDateStr = outputFormat.format(new_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateStr;
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView date_of_order, order_number, order_items, order_status, grand_total;
        public CardView orderReceivedCard;

        public ViewHolder(View itemview){
            super(itemview);
            date_of_order = itemview.findViewById(R.id.date_of_order);
            order_number = itemview.findViewById(R.id.order_number);
            order_items =  itemview.findViewById(R.id.order_items);
            order_status = itemview.findViewById(R.id.order_status);
            grand_total = itemview.findViewById(R.id.grand_total);
            orderReceivedCard = itemview.findViewById(R.id.orderReceivedCard);

        }
    }
}
