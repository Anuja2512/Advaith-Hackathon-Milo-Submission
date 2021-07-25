package com.example.milo;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    String fprofileimage;
    Button updateprofile;
    String token, newimageurl="";
    CircleImageView profiledp;
    TextView something;
    Uri uri;
    Bitmap bitmapImage;
    private ProgressDialog loadingBar;
    EditText fnametext,lnametext,usernametext,emailtext,headlinetext,aboutMetext,localitytext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Dashboard activity = (Dashboard)getActivity();
        token = activity.getToken();
        String username = activity.getUsername();
        loadingBar=new ProgressDialog(activity);
        updateprofile=view.findViewById(R.id.updateprofilebtn);
        something=view.findViewById(R.id.textView);
        fnametext=view.findViewById(R.id.fname);
        loadingBar.setTitle("Profile Update");
        loadingBar.setMessage("Please wait, while we fetch your account.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        lnametext=view.findViewById(R.id.lname);
        usernametext=view.findViewById(R.id.fusername);
        profiledp=view.findViewById(R.id.profilepic);
        profiledp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    startGallery();
                }
            }
        });
        emailtext=view.findViewById(R.id.femail);
        headlinetext=view.findViewById(R.id.fheadline);
        aboutMetext=view.findViewById(R.id.faboutme);
        localitytext=view.findViewById(R.id.flocality);
        usernametext.setClickable(false);
        usernametext.setEnabled(false);
        usernametext.setTextIsSelectable(false);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://milo-backend.deta.dev/api/profile/"+username)
                .get()
                .addHeader("Authorization", token)
                .build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //
                            try {
                                //Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
                                JSONObject jsonObject=new JSONObject(res);
                                String fusername = jsonObject.getString("username");
                                String ffirstname = jsonObject.getString("fName");
                                String flastname = jsonObject.getString("lName");
                                String femail = jsonObject.getString("email");
                                String fheadline =jsonObject.getString("headline");
                                String faboutMe = jsonObject.getString("aboutMe");
                                String flocality = jsonObject.getString("locality");
                                fprofileimage = jsonObject.getString("profilePicture");
                                if(!fprofileimage.equals(""))
                                {
                                    Thread thread1 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL url = new URL(fprofileimage);
                                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        profiledp.setImageBitmap(image);
                                                        loadingBar.dismiss();
                                                    }
                                                });
                                            } catch(IOException e) {
                                                System.out.println(e);
                                            }
                                        }
                                    });
                                    thread1.start();
                                }


                                usernametext.setText(fusername);
                                fnametext.setText(ffirstname);
                                lnametext.setText(flastname);
                                emailtext.setText(femail);
                                headlinetext.setText(fheadline);
                                aboutMetext.setText(faboutMe);
                                localitytext.setText(flocality);
                                loadingBar.dismiss();
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();

        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fnametext.getText().equals(""))
                {
                    fnametext.setError("Please enter your First name");
                }
                else if(lnametext.getText().equals(""))
                {
                    lnametext.setError("Please enter your Last Name");
                }
                else if(emailtext.getText().equals(""))
                {
                    emailtext.setError("Please enter an email Id");
                }
                else if(headlinetext.getText().equals(""))
                {
                    headlinetext.setError("Please enter your headline");
                }
                else if(localitytext.getText().equals(""))
                {
                    localitytext.setError("Please enter your locality");
                }
                else if(aboutMetext.getText().equals(""))
                {
                    aboutMetext.setError("Please tell us a little about you!");
                }
                else
                {
                    loadingBar.show();
                    com.android.volley.RequestQueue requestQueue= com.android.volley.toolbox.Volley.newRequestQueue(getActivity());
                    JSONObject postData = new JSONObject();
                    try{
                        postData.put("fName", fnametext.getText().toString());
                        postData.put("lName", lnametext.getText().toString());
                        postData.put("email", emailtext.getText().toString());
                        postData.put("headline", headlinetext.getText().toString());
                        postData.put("aboutMe", aboutMetext.getText().toString());
                       // Toast.makeText(getActivity(), newimageurl, Toast.LENGTH_SHORT).show();
                        if(newimageurl.equals(""))
                        {
                            postData.put("profilePicture", fprofileimage);
                        }
                        else
                        {
                            postData.put("profilePicture",newimageurl );
                        }
                        postData.put("locality", localitytext.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = "https://milo-backend.deta.dev/api/profile/"+usernametext.getText().toString();
                    com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest = new com.android.volley.toolbox.JsonObjectRequest(com.android.volley.Request.Method.PUT, url, postData, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject jsonObject = response;
                            try {
                                String status = jsonObject.getString("status");
                                if(status.equals("200"))
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(activity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(activity, "Failed to Update", Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }

                        }
                    }, new com.android.volley.Response.ErrorListener() {


                        @Override
                        public void onErrorResponse(com.android.volley.VolleyError error) {

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError{
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("Authorization", token);
                            return params;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);

                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public static final String UPLOAD_URL = "https://milo-backend.deta.dev/api/uploadimage";
    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            Uri returnUri;
            returnUri = data.getData();
          //  Toast.makeText(getActivity(), returnUri.getPath(), Toast.LENGTH_SHORT).show();
            bitmapImage = null;
            try {
                loadingBar.show();
                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                profiledp.setImageBitmap(bitmapImage);


                byte [] bytesofimage = byteArrayOutputStream.toByteArray();
                File file = getActivity().getFilesDir();
                File imageFile = new File(file, "" + ".jpg");
                OutputStream os;
                try {
                    os = new FileOutputStream(imageFile);
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody body= new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "jpg", RequestBody.create(MediaType.parse("multipart/form-data"), imageFile))
                        .build();
                Request request=new Request.Builder()
                        .url("https://milo-backend.deta.dev/api/uploadimage")
                        .addHeader("Authorization", token)
                        .post(body)
                        .build();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = client.newCall(request).execute();
                            String res = response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   // Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject object = new JSONObject(res);
                                        String status = object.getString("status");
                                        if(status.equals("200"))
                                        {
                                            newimageurl=object.getString("link");
                                            loadingBar.dismiss();
                                           // Toast.makeText(getActivity(), "Click on update profile ", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();
                                    }


                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
                thread.start();


                //something.setText(encodedString);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}