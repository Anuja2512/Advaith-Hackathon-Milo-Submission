package com.example.milo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class SignInActivity extends AppCompatActivity {
    EditText emailtext, passtext;
    Button signin;
    TextView notauser;
    public static String PREFS_NAME="MyPresFile";
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://source.unsplash.com/random");
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    SignInActivity.this.runOnUiThread(new Runnable() {
                        @SuppressLint("Range")
                        @Override
                        public void run() {
                            ConstraintLayout relativeLayout = findViewById(R.id.layout);
                            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                            relativeLayout.setBackground(new BitmapDrawable(image));
                            relativeLayout.getBackground().setAlpha(100);
                            //profiledp.setImageBitmap(image);
                        }
                    });
                } catch(IOException e) {
                    System.out.println(e);
                }
            }
        });
        thread1.start();
        emailtext = findViewById(R.id.mail);
        passtext = findViewById(R.id.password);
        signin = findViewById(R.id.signinbutton);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailtext.getText().equals(""))
                {
                    emailtext.setError("Enter an email!");
                }
                if(passtext.getText().equals(""))
                {
                    passtext.setError("Enter a password!");
                }
                else
                {

                    final String url = "https://milo-backend.deta.dev/api/login";
                    String email= emailtext.getText().toString();
                    String pass = passtext.getText().toString();

                    RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", email);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    try {
                        object.put("password", pass);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject jsonObject = response;
                            try {
                                String message = jsonObject.getString("message");
                                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                                String status = jsonObject.getString("status");
                                if(status.equals("200"))
                                {
                                    String token = jsonObject.getString("token");
                                    String username = jsonObject.getString("username");
                                    String firstname = jsonObject.getString("fName");
                                    String lastname = jsonObject.getString("lName");
                                    String email = jsonObject.getString("email");
                                    SharedPreferences sharedPreferences=getSharedPreferences(SignInActivity.PREFS_NAME, 0);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putBoolean("hasLoggedIn", true);
                                    editor.putString("username",username );
                                    editor.putString("token",token);
                                    editor.putString("lastname",lastname );
                                    editor.putString("firstname",firstname);
                                    editor.putString("email",email);
                                    editor.commit();
                                    Intent intent = new Intent(SignInActivity.this, Dashboard.class);
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

                    /*Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SignInActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    });
                    thread.start();*/
                }
            }
        });
        notauser = findViewById(R.id.textView3);
        notauser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}