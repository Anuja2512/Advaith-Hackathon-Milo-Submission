package com.example.milo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    Button Submit;
    TextView gotoLogin;
    RequestQueue requestQueue;
    ImageView profilepic;
    private static String URL_REGIST = "https://asia-south1-milo-node.cloudfunctions.net/api/signup";
    EditText fname,lname,username,email,headline,aboutMe,locality,password,cpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://source.unsplash.com/random");
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @SuppressLint("Range")
                        @Override
                        public void run() {
                            ScrollView relativeLayout = findViewById(R.id.layout);
                            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                            relativeLayout.setBackground(new BitmapDrawable(image));
                            relativeLayout.getBackground().setAlpha(200);
                            //profiledp.setImageBitmap(image);
                        }
                    });
                } catch(IOException e) {
                    System.out.println(e);
                }
            }
        });
        thread1.start();
        fname=findViewById(R.id.RTextboxFName);
        lname=findViewById(R.id.RTextboxLName);
        username=findViewById(R.id.RTextboxUsername);
        email=findViewById(R.id.RTextboxEmail);
        headline=findViewById(R.id.RTextboxHeadline);
        aboutMe=findViewById(R.id.RTextboxAboutme);
        locality=findViewById(R.id.RTextboxLocality);
        password=findViewById(R.id.RTextboxPassword);
        cpassword=findViewById(R.id.RTextboxCPassword);
        gotoLogin=findViewById(R.id.gotoLogin);
        profilepic=findViewById(R.id.profilepic);
        Submit=findViewById(R.id.RegisterBtn);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(fname.getText().toString())){
                    fname.setError("Please enter your first name");
                    fname.requestFocus();
                }
                else if(TextUtils.isEmpty(lname.getText().toString())){
                    lname.setError("Please enter your last name");
                    lname.requestFocus();
                }
                else if(TextUtils.isEmpty(username.getText().toString())){
                    username.setError("Please enter your username");
                    username.requestFocus();
                }
                else if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Please enter your email");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(headline.getText().toString())){
                    headline.setError("Please enter your headline");
                    headline.requestFocus();
                }
                else if(TextUtils.isEmpty(aboutMe.getText().toString())){
                    aboutMe.setError("Please enter about yourself");
                    aboutMe.requestFocus();
                }
                else if(TextUtils.isEmpty(locality.getText().toString())){
                    locality.setError("Please enter your locality");
                    locality.requestFocus();
                }
                else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(TextUtils.isEmpty(cpassword.getText().toString())){
                    cpassword.setError("Please confirm your password");
                    cpassword.requestFocus();
                }
                else if(!cpassword.getText().toString().equals(password.getText().toString()))
                {
                    cpassword.setError("Passwords don't match!");
                    cpassword.setError("Passwords don't match!");
                }
                else{
                    RequestQueue requestQueue= Volley.newRequestQueue(SignUpActivity.this);
                    JSONObject postData = new JSONObject();
                    try{
                        postData.put("fName", fname.getText().toString());
                        postData.put("lName", lname.getText().toString());
                        postData.put("username", username.getText().toString());
                        postData.put("email", email.getText().toString());
                        postData.put("headline", headline.getText().toString());
                        postData.put("aboutMe", aboutMe.getText().toString());
                        postData.put("password", password.getText().toString());
                        postData.put("profilePicture", "");
                        postData.put("locality", locality.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, URL_REGIST,postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response){
                            JSONObject jsonObject = response;
                            try {
                                String message = jsonObject.getString("message");
                                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                                String status = jsonObject.getString("status");
                                if(status.equals("201"))
                                {
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

                    requestQueue.add(jsonObjectRequest);
                }

            }
        });
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            Long today = MaterialDatePicker.todayInUtcMilliseconds();
//            Instant instant = Instant.now();
//            Long epochValue = instant.getEpochSecond();
//            Toast.makeText(this, epochValue.toString(), Toast.LENGTH_SHORT).show();
//        }

    }
}