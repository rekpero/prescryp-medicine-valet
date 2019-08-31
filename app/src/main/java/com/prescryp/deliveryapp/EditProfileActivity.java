package com.prescryp.deliveryapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescryp.deliveryapp.SessionManager.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView profileImg;
    TextView editPhoto;
    TextInputLayout name_text, phone_number_text, email_text;
    CardView updateCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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


        profileImg = (CircleImageView) findViewById(R.id.profile_pic);
        name_text = (TextInputLayout) findViewById(R.id.name_input);
        phone_number_text = (TextInputLayout) findViewById(R.id.phone_no_input);
        email_text = (TextInputLayout) findViewById(R.id.email_input);
        updateCard = (CardView) findViewById(R.id.saveProfileUpdate);

        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        final HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String session_name = user.get(UserSessionManager.KEY_NAME);
        
        getEmailID(session_mob, session_name);

        updateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail(email_text.getEditText().getText().toString(), session_mob);
            }
        });
    }

    private void updateEmail(final String email, final String session_mob) {
        String url =  "http://prescryp.com/prescriptionUpload/updateValetEmail.php";
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

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){

                                onBackPressed();


                            }else if (success.equals("2")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(EditProfileActivity.this).add(stringRequest);
    }

    private void getEmailID(final String session_mob, final String session_name) {
        String url =  "http://prescryp.com/prescriptionUpload/getEmailValet.php";
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

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){

                                String name = jsonObject.getString("name");
                                String email = jsonObject.getString("email");
                                if (name.equalsIgnoreCase(session_name)){
                                    name_text.getEditText().setText(session_name);
                                    name_text.getEditText().setEnabled(false);
                                }
                                phone_number_text.getEditText().setText(session_mob);
                                phone_number_text.getEditText().setEnabled(false);

                                email_text.getEditText().setText(email);


                            }else if (success.equals("2")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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

        Volley.newRequestQueue(EditProfileActivity.this).add(stringRequest);
    }

}
