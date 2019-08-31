package com.prescryp.deliveryapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.google.android.gms.location.LocationRequest;
import com.prescryp.deliveryapp.Adapter.PickupListAdapter;
import com.prescryp.deliveryapp.Misc.RunTimePermission;
import com.prescryp.deliveryapp.Model.OrderItem;
import com.prescryp.deliveryapp.Model.PickupItem;
import com.prescryp.deliveryapp.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class OrderDetailsActivity extends AppCompatActivity {
    private String order_number, order_status, patient_mobile_number;
    private TextView date_of_order_text, patient_name_text, locality_text, complete_delivery_address_text, delivery_instruction_text;
    private TextView amount_text, paid_via_text, date_of_delivery,date_of_delivery_heading, dispatch_verification_otp;
    private ConstraintLayout call_patient_layout, start_navigation, get_dispatch_otp_layout, delivered_layout, otp_layout;
    private RunTimePermission photoRunTimePermission;
    private List<PickupItem> pickupItemList;
    private RecyclerView pickup_recycler_view;
    private ProgressBar loading_order_delivery;
    private ScrollView delivery_order_view;
    private CountDownLatch latch, latch1;
    private ProgressDialog mDialog;
    private View divider3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent() != null){
            order_number = getIntent().getStringExtra("Order_Number");
        }

        String title = "Order #" + order_number;
        toolbar.setTitle(title);

        initUI();
        pickupItemList = new ArrayList<>();
        latch = new CountDownLatch(2);

        getOrderForTracking(order_number);

        getOrderItem(order_number);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    OrderDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading_order_delivery.setVisibility(View.GONE);
                            if (order_status.equalsIgnoreCase("Delivered")){
                                delivered_layout.setVisibility(View.GONE);
                            }else {
                                delivered_layout.setVisibility(View.VISIBLE);
                            }
                            delivery_order_view.setVisibility(View.VISIBLE);

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        get_dispatch_otp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVerificationCode(order_number);
            }
        });

        delivered_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVerificationPopup();
            }
        });


    }



    private void initUI() {
        date_of_order_text = findViewById(R.id.date_of_order);
        patient_name_text = findViewById(R.id.patient_name_text);
        locality_text = findViewById(R.id.locality_text);
        complete_delivery_address_text = findViewById(R.id.complete_delivery_address_text);
        delivery_instruction_text = findViewById(R.id.delivery_instruction_text);
        amount_text = findViewById(R.id.amount_text);
        paid_via_text = findViewById(R.id.paid_via_text);
        date_of_delivery = findViewById(R.id.date_of_delivery);
        date_of_delivery_heading = findViewById(R.id.date_of_delivery_heading);
        call_patient_layout = findViewById(R.id.call_patient_layout);
        start_navigation = findViewById(R.id.start_navigation);
        pickup_recycler_view = findViewById(R.id.pickup_recycler_view);
        get_dispatch_otp_layout = findViewById(R.id.get_dispatch_otp_layout);
        dispatch_verification_otp = findViewById(R.id.dispatch_verification_otp);
        loading_order_delivery = findViewById(R.id.loading_order_delivery);
        delivery_order_view = findViewById(R.id.delivery_order_view);
        delivered_layout = findViewById(R.id.delivered_layout);
        otp_layout = findViewById(R.id.otp_layout);
        divider3 = findViewById(R.id.divider3);
    }

    private void showVerificationPopup() {
        final PinView otp_pin;
        TextView submit_code, cancel_code;
        final Dialog otpDialog = new Dialog(OrderDetailsActivity.this);
        otpDialog.setContentView(R.layout.verification_otp_popup);
        otp_pin = otpDialog.findViewById(R.id.otp_pin);
        submit_code = otpDialog.findViewById(R.id.submit_code);
        cancel_code = otpDialog.findViewById(R.id.cancel_code);

        cancel_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpDialog.dismiss();
            }
        });

        submit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmVerificationCode(order_number, otp_pin.getText().toString(), otpDialog);
            }
        });

        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.show();

    }

    private void confirmVerificationCode(final String order_number, final String otp_code, final Dialog otpDialog) {
        String url = "http://prescryp.com/prescriptionUpload/verifyPatient.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        otpDialog.dismiss();
                        mDialog = new ProgressDialog(OrderDetailsActivity.this);
                        mDialog.setMessage("Dispatching Order...");
                        mDialog.show();
                        deliveredUpdate(order_number);

                    }else if (success.equalsIgnoreCase("2")){
                        showVerificationPopup();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("verification_code", otp_code);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void deliveredUpdate(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/updateDelivered.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        deliverOrderToPatientFCM(patient_mobile_number, order_number);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void deliverOrderToPatientFCM(final String mobile_number, final String order_number) {
        final String title = "Your Order #" + String.valueOf(order_number) + " has been Delivered";
        final String message = "Your order has been delivered. Don't forget to rate us for our services.";
        String url = "http://prescryp.com/prescriptionUpload/sendNotification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equalsIgnoreCase("1")){

                        sendNotificationChemists();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("mobile_number", mobile_number);
                params.put("title", title);
                params.put("message", message);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void sendNotificationChemists() {
        latch1 = new CountDownLatch(pickupItemList.size());
        for (PickupItem pickupItem : pickupItemList){
            deliverOrderForChemistFCM(pickupItem.getSellerMobileNumber(), order_number);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch1.await();
                    OrderDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Delivered Successfully", Toast.LENGTH_SHORT).show();
                            delivered_layout.setVisibility(View.GONE);
                            mDialog.dismiss();

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deliverOrderForChemistFCM(final String sellerMobileNumber, final String order_number) {
        final String title = "The Order #" + String.valueOf(order_number) + " from your store has been Delivered";
        final String message = "The order has been delivered. Checkout your earning from this order.";
        String url = "http://prescryp.com/prescriptionUpload/sendNotification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equalsIgnoreCase("1")){

                    }
                    latch1.countDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("mobile_number", sellerMobileNumber);
                params.put("title", title);
                params.put("message", message);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getVerificationCode(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/insertGeneratedOtp.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1") || success.equalsIgnoreCase("3")){
                        String delivery_person_verification_code = jsonObject.getString("delivery_person_verification_code");

                        dispatch_verification_otp.setText(delivery_person_verification_code);
                        get_dispatch_otp_layout.setVisibility(View.GONE);
                        dispatch_verification_otp.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getOrderForTracking(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/getOrderDetailsForDelivery.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    String date_of_order = jsonObject.getString("date_of_order");
                    String time_of_order = jsonObject.getString("time_of_order");
                    String grand_total = jsonObject.getString("grand_total");
                    String paid_or_not = jsonObject.getString("paid_or_not");
                    order_status = jsonObject.getString("order_status");
                    String payment_type = jsonObject.getString("payment_type");
                    patient_mobile_number = jsonObject.getString("patient_mobile_number");
                    String patient_name = jsonObject.getString("patient_name");
                    String delivery_locality = jsonObject.getString("delivery_locality");
                    String delivery_complete_address = jsonObject.getString("delivery_complete_address");
                    String delivery_instruction = jsonObject.getString("delivery_instruction");
                    final String delivery_latitude = jsonObject.getString("delivery_latitude");
                    final String delivery_longitude = jsonObject.getString("delivery_longitude");
                    String delivery_date = jsonObject.getString("delivery_date");
                    String delivery_time = jsonObject.getString("delivery_time");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        if (order_status.equalsIgnoreCase("Delivered")){
                            String delivery_date_change_format = getDateFormatChange(delivery_date);
                            String[] delivery_time_list = delivery_time.split(" ");
                            String delivery_time_changed = delivery_time_list[0] + " " + delivery_time_list[1].toUpperCase();

                            String delivery_date_shown = delivery_date_change_format.trim() + ", " + delivery_time_changed.trim();
                            date_of_delivery.setText(delivery_date_shown);
                            otp_layout.setVisibility(View.GONE);
                            divider3.setVisibility(View.GONE);
                        }else if (order_status.equalsIgnoreCase("Dispatched")){
                            otp_layout.setVisibility(View.GONE);
                            divider3.setVisibility(View.GONE);
                            date_of_delivery_heading.setVisibility(View.GONE);
                            date_of_delivery.setVisibility(View.GONE);
                        }else {
                            date_of_delivery_heading.setVisibility(View.GONE);
                            date_of_delivery.setVisibility(View.GONE);
                        }

                        String date_change_format = getDateFormatChange(date_of_order);
                        String[] time_list = time_of_order.split(" ");
                        String time_changed = time_list[0] + " " + time_list[1].toUpperCase();

                        String date_shown = date_change_format.trim() + ", " + time_changed.trim();
                        date_of_order_text.setText(date_shown);

                        Locale locale = new Locale("hi", "IN");
                        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);

                        String amount_shown = paid_or_not + " : " + nf.format(Float.valueOf(grand_total));
                        amount_text.setText(amount_shown);

                        String paid_via = "Paid : " + payment_type;
                        paid_via_text.setText(paid_via);

                        patient_name_text.setText(patient_name);

                        locality_text.setText(delivery_locality);
                        complete_delivery_address_text.setText(delivery_complete_address);
                        delivery_instruction_text.setText(delivery_instruction);

                        call_patient_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Intent intent = new Intent(Intent.ACTION_CALL);

                                intent.setData(Uri.parse("tel:" + patient_mobile_number));
                                if (ActivityCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    startActivity(intent);
                                } else {
                                    photoRunTimePermission = new RunTimePermission(OrderDetailsActivity.this);
                                    photoRunTimePermission.requestPermission(new String[]{
                                            Manifest.permission.CALL_PHONE
                                    }, new RunTimePermission.RunTimePermissionListener() {

                                        @Override
                                        public void permissionGranted() {
                                            if (ActivityCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                startActivity(intent);
                                            }

                                        }

                                        @Override
                                        public void permissionDenied() {
                                        }
                                    });
                                }

                            }

                        });

                        start_navigation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String nav_uri = "google.navigation:q=" + delivery_latitude.trim() + "," + delivery_longitude.trim() + "&avoid=tf";
                                Uri gmmIntentUri = Uri.parse(nav_uri);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });

                    }
                    latch.countDown();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
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
    private void getOrderItem(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/getOrderItemDelivery.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("order_items");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        for (int i = 0; i < jsonArray.length(); i++) {

                            //getting product object from json array
                            JSONObject order_item = jsonArray.getJSONObject(i);
                            String medicine_name = order_item.getString("medicine_name");
                            String package_contain = order_item.getString("package_contain");
                            String quantity = order_item.getString("quantity");
                            String item_status = order_item.getString("order_status");
                            String seller_mobile_number = order_item.getString("seller_mobile_number");
                            String seller_name = order_item.getString("seller_name");
                            String store_contact = order_item.getString("store_contact");
                            String store_address = order_item.getString("store_address");
                            String latitude = order_item.getString("latitude");
                            String longitude = order_item.getString("longitude");


                            boolean contains = false;
                            for (PickupItem item : pickupItemList){
                                if (item.getSellerMobileNumber().equalsIgnoreCase(seller_mobile_number)){
                                    item.getOrderItemsList().add(new OrderItem(medicine_name, quantity, package_contain, item_status));
                                    contains = true;
                                }
                            }

                            if (!contains){
                                List<OrderItem> order_item_list = new ArrayList<>();
                                order_item_list.add(new OrderItem(medicine_name, quantity, package_contain, item_status));
                                pickupItemList.add(new PickupItem(seller_mobile_number, seller_name, store_contact, store_address, latitude, longitude, order_item_list));
                            }
                        }

                        PickupListAdapter adapter = new PickupListAdapter(pickupItemList, OrderDetailsActivity.this, OrderDetailsActivity.this);
                        pickup_recycler_view.setAdapter(adapter);
                        pickup_recycler_view.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
                        pickup_recycler_view.setHasFixedSize(true);


                    }
                    latch.countDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

}
