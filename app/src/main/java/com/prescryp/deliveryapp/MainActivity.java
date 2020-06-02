package com.prescryp.deliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.prescryp.deliveryapp.Adapter.OrderRecievedListAdapter;
import com.prescryp.deliveryapp.Misc.Converter;
import com.prescryp.deliveryapp.Model.NotificationItem;
import com.prescryp.deliveryapp.Model.OrderReceivedItem;
import com.prescryp.deliveryapp.SessionManager.UserSessionManager;
import com.prescryp.deliveryapp.database.NotificationsDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private List<NotificationItem> notificationItem;
    private static int notification_count = 0;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setCurrentItem(0);

        notificationItem = new NotificationsDBHelper(this).getNotificationList();
        notification_count = notificationItem.size();
        invalidateOptionsMenu();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        notificationItem = new NotificationsDBHelper(this).getNotificationList();
        notification_count = notificationItem.size();

        invalidateOptionsMenu();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem notification = menu.findItem(R.id.action_notification);
        notification.setIcon(Converter.convertLayoutToImage(MainActivity.this, notification_count, R.mipmap.ic_bell_white_icon));
        notification.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                return true;
            }
        });

        MenuItem account = menu.findItem(R.id.action_account);
        account.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, UserAccountActivity.class));
                return true;
            }
        });


        return true;
    }

    public static class CurrentFragment extends Fragment {

        private RecyclerView current_recycler_view;
        private List<OrderReceivedItem> receivedItemsList;
        private TextView currentTextView;

        public CurrentFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_current, container, false);
            current_recycler_view = rootView.findViewById(R.id.current_recycler_view);
            currentTextView = rootView.findViewById(R.id.currentTextView);
            receivedItemsList = new ArrayList<>();
            getAllOrderReceived();
            return rootView;
        }

        private void getAllOrderReceived() {
            UserSessionManager userSessionManager = new UserSessionManager(getActivity());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getAllOrderForDelivery.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("order_history");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list

                                        String order_number = orders.getString("order_number");
                                        String date_of_order = orders.getString("date_of_order");
                                        String time_of_order = orders.getString("time_of_order");
                                        String order_status = orders.getString("order_status");
                                        String grand_total = orders.getString("grand_total");
                                        String medicine_name = orders.getString("medicine_name");
                                        String quantity = orders.getString("quantity");
                                        String order_item = quantity + " \u00D7 " + medicine_name;

                                        if (order_status.equalsIgnoreCase("Dispatched")){
                                            boolean contains = false;
                                            for (OrderReceivedItem item : receivedItemsList){
                                                if (item.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    item.getOrderItems().add(order_item);
                                                    contains = true;
                                                }
                                            }

                                            if (!contains){
                                                List<String> order_item_list = new ArrayList<>();
                                                order_item_list.add(order_item);
                                                receivedItemsList.add(new OrderReceivedItem(order_number, date_of_order, time_of_order, order_status,
                                                        grand_total, order_item_list));
                                            }
                                        }




                                    }

                                    if (receivedItemsList.size() == 0){
                                        currentTextView.setVisibility(View.VISIBLE);
                                    }

                                    OrderRecievedListAdapter adapter = new OrderRecievedListAdapter(receivedItemsList, getActivity());
                                    current_recycler_view.setAdapter(adapter);
                                    current_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
                                    current_recycler_view.setHasFixedSize(true);




                                }else if (success.equals("2")){
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    return params;
                }
            };

            Volley.newRequestQueue(getActivity()).add(stringRequest);

        }
    }

    public static class PendingFragment extends Fragment {

        private RecyclerView pending_recycler_view;
        private List<OrderReceivedItem> receivedItemsList;
        private TextView pendingTextView;

        public PendingFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pending, container, false);
            pending_recycler_view = rootView.findViewById(R.id.pending_recycler_view);
            pendingTextView = rootView.findViewById(R.id.pendingTextView);


            receivedItemsList = new ArrayList<>();
            getAllOrderReceived();
            return rootView;
        }

        private void getAllOrderReceived() {
            UserSessionManager userSessionManager = new UserSessionManager(getActivity());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getAllOrderForDelivery.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("order_history");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list

                                        String order_number = orders.getString("order_number");
                                        String date_of_order = orders.getString("date_of_order");
                                        String time_of_order = orders.getString("time_of_order");
                                        String order_status = orders.getString("order_status");
                                        String grand_total = orders.getString("grand_total");
                                        String medicine_name = orders.getString("medicine_name");
                                        String quantity = orders.getString("quantity");
                                        String order_item = quantity + " \u00D7 " + medicine_name;

                                        if (order_status.equalsIgnoreCase("Confirmed")){
                                            boolean contains = false;
                                            for (OrderReceivedItem item : receivedItemsList){
                                                if (item.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    item.getOrderItems().add(order_item);
                                                    contains = true;
                                                }
                                            }

                                            if (!contains){
                                                List<String> order_item_list = new ArrayList<>();
                                                order_item_list.add(order_item);
                                                receivedItemsList.add(new OrderReceivedItem(order_number, date_of_order, time_of_order, order_status,
                                                        grand_total, order_item_list));
                                            }
                                        }




                                    }
                                    if (receivedItemsList.size() == 0){
                                        pendingTextView.setVisibility(View.VISIBLE);
                                    }

                                    OrderRecievedListAdapter adapter = new OrderRecievedListAdapter(receivedItemsList, getActivity());
                                    pending_recycler_view.setAdapter(adapter);
                                    pending_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
                                    pending_recycler_view.setHasFixedSize(true);




                                }else if (success.equals("2")){
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    return params;
                }
            };

            Volley.newRequestQueue(getActivity()).add(stringRequest);

        }
    }

    public static class CompletedFragment extends Fragment {

        private RecyclerView completed_recycler_view;
        private List<OrderReceivedItem> receivedItemsList;
        private TextView completedTextView;

        public CompletedFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_completed, container, false);
            completed_recycler_view = rootView.findViewById(R.id.completed_recycler_view);
            completedTextView = rootView.findViewById(R.id.completedTextView);

            receivedItemsList = new ArrayList<>();
            getAllOrderReceived();
            return rootView;
        }

        private void getAllOrderReceived() {
            UserSessionManager userSessionManager = new UserSessionManager(getActivity());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getAllOrderForDelivery.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("order_history");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list

                                        String order_number = orders.getString("order_number");
                                        String date_of_order = orders.getString("date_of_order");
                                        String time_of_order = orders.getString("time_of_order");
                                        String order_status = orders.getString("order_status");
                                        String grand_total = orders.getString("grand_total");
                                        String medicine_name = orders.getString("medicine_name");
                                        String quantity = orders.getString("quantity");
                                        String order_item = quantity + " \u00D7 " + medicine_name;

                                        if (order_status.equalsIgnoreCase("Delivered")){
                                            boolean contains = false;
                                            for (OrderReceivedItem item : receivedItemsList){
                                                if (item.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    item.getOrderItems().add(order_item);
                                                    contains = true;
                                                }
                                            }

                                            if (!contains){
                                                List<String> order_item_list = new ArrayList<>();
                                                order_item_list.add(order_item);
                                                receivedItemsList.add(new OrderReceivedItem(order_number, date_of_order, time_of_order, order_status,
                                                        grand_total, order_item_list));
                                            }
                                        }




                                    }

                                    if (receivedItemsList.size() == 0){
                                        completedTextView.setVisibility(View.VISIBLE);
                                    }

                                    OrderRecievedListAdapter adapter = new OrderRecievedListAdapter(receivedItemsList, getActivity());
                                    completed_recycler_view.setAdapter(adapter);
                                    completed_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
                                    completed_recycler_view.setHasFixedSize(true);




                                }else if (success.equals("2")){
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    return params;
                }
            };

            Volley.newRequestQueue(getActivity()).add(stringRequest);

        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0 :
                    return new CurrentFragment();
                case 1 :
                    return new PendingFragment();
                case 2 :
                    return new CompletedFragment();

                default: return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
