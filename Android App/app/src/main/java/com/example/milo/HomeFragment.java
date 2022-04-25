package com.example.milo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements LocationListener {
    JSONObject postData;
    RecyclerView recyclerView;
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    FeedAdapter feedAdapter;
    TextView seekbartext, addhashtag;
    SeekBar seekbar;
    EditText postcontenttext, hashtagtext;
    TextInputLayout hashtagbox;
    String token, latitudee, longitude;
    ImageView postbutton, picturebutton, postimage;
    String newimageurl = "";
    Bitmap bitmapImage;
    LocationManager locationManager;
    JsonObjectRequest jsonObjectRequest;
    String username;
    private GoogleMap mMap;
    Button btn;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
    RequestQueue requestQueue;
    Geocoder geocoder;
    private ProgressDialog loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Dashboard activity = (Dashboard) getActivity();
        username = activity.getUsername();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        fetchLastLocation();
        token = activity.getToken();
        loadingBar = new ProgressDialog(activity);
        loadingBar.setTitle("Please Wait");
        loadingBar.setMessage("Loading!");
        recyclerView = view.findViewById(R.id.FeedRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        feedAdapter = new FeedAdapter(getActivity(), modelFeedArrayList);
        recyclerView.setAdapter(feedAdapter);
        modelFeedArrayList.clear();
        // Toast.makeText(activity, username, Toast.LENGTH_SHORT).show();
        addhashtag = view.findViewById(R.id.hashtag);
        hashtagbox = view.findViewById(R.id.hashtagbox);
        hashtagtext = view.findViewById(R.id.hashtagtext);
        seekbar = view.findViewById(R.id.seekBar);
        seekbar.setProgress(1);
        seekbartext = view.findViewById(R.id.seekbartext);
        postcontenttext = view.findViewById(R.id.posttext);
        postbutton = view.findViewById(R.id.postbutton);
        postimage = view.findViewById(R.id.postimage);
        picturebutton = view.findViewById(R.id.picturebutton);
        geocoder = new Geocoder(getActivity());

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        picturebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                } else {
                    startGallery();
                }
            }
        });

        addhashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashtagbox.setVisibility(View.VISIBLE);
                addhashtag.setVisibility(View.INVISIBLE);
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekbartext.setText(i + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postcontenttext.getText().toString().equals("")) {
                    postcontenttext.setError("Can't send an Empty post!");
                } else {
                    String post = postcontenttext.getText().toString();

                    fetchLastLocation();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Instant instant = Instant.now();
                        Long epochValue = instant.getEpochSecond();
                        //Toast.makeText(getActivity(), epochValue.toString(), Toast.LENGTH_SHORT).show();
                        Integer radiusint = seekbar.getProgress();
                        String radius = radiusint.toString();
                        String hashtagstring = hashtagtext.getText().toString();
                        Pattern p = Pattern.compile("#\\S+");
                        List<String> hashTags = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray();
                        Matcher matcher = p.matcher(hashtagstring);
                        while (matcher.find()) {
                            hashTags.add(matcher.group(0));
                            jsonArray.put(matcher.group(0).toLowerCase());
                        }
                        loadingBar.show();
                        postData = new JSONObject();
                        try {
                            postData.put("username", username);
                            postData.put("data", post);
                            if (newimageurl.equals("")) {
                                postData.put("imageLink", "");
                            } else {
                                postData.put("imageLink", newimageurl);
                            }
                            // Toast.makeText(activity, jsonArray.toString(), Toast.LENGTH_SHORT).show();
                            postData.put("hashtags", jsonArray);
                            postData.put("latitude", latitudee);
                            postData.put("longitude", longitude);
                            postData.put("radius", radius);
                            postData.put("timeEpoch", epochValue.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.contextOfApplication);
                        // postrequest();
                        OkHttpClient client = new OkHttpClient();
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        // put your json here
                        RequestBody body = RequestBody.create(JSON, postData.toString());
                        Request request = new Request.Builder()
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/posts")
                                .addHeader("Authorization", token)
                                .post(body)
                                .build();
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Response response = client.newCall(request).execute();
                                    String resStr = response.body().string();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject object = new JSONObject(resStr);
                                                String key = object.getString("_id");
                                                if (key.equals(null)) {
                                                    Toast.makeText(getActivity(), "Could not post, Please try again!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    loadingBar.dismiss();
                                                    postimage.setVisibility(View.GONE);
                                                    postcontenttext.setText("");
                                                    hashtagbox.setVisibility(View.GONE);
                                                    addhashtag.setVisibility(View.VISIBLE);
                                                    seekbar.setProgress(1);
                                                    Toast.makeText(getActivity(), "Post Successful", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            //Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread1.start();


                        //requestQueue.add(jsonObjectRequest);
                    }
                }
            }
        });

        return view;
    }


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
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                postimage.setImageBitmap(bitmapImage);
                postimage.setVisibility(View.VISIBLE);
                byte[] bytesofimage = byteArrayOutputStream.toByteArray();
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
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "jpg", RequestBody.create(MediaType.parse("multipart/form-data"), imageFile))
                        .build();
                Request request = new Request.Builder()
                        .url("https://asia-south1-milo-node.cloudfunctions.net/api/uploadimage")
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
                                        if (status.equals("200")) {
                                            newimageurl = object.getString("link");
                                            //  Toast.makeText(getActivity(), newimageurl, Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getActivity(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                                        }
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


                //something.setText(encodedString);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {


        try {
            locationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // Toast.makeText(getActivity(), ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            Toast.makeText(getActivity(), "hiiii", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //https://github.com/Harsh-Singh-007/Essential_Services/blob/6903972795e0116a33f568add4dd6633121cf9d5/app/src/main/java/com/example/find/maps.java

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fetchLastLocation() {
        Dashboard activity = (Dashboard) getActivity();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        // Toast.makeText(getActivity(),currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                        Double latitude = currentLocation.getLatitude();
                        Double longit = currentLocation.getLongitude();
                        latitudee = latitude.toString();
                        longitude = longit.toString();
                        OkHttpClient client = new OkHttpClient();
                        //Toast.makeText(activity, latitudee + longitude, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, username, Toast.LENGTH_SHORT).show();
                       // Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, "https://asia-south1-milo-node.cloudfunctions.net/api/feed/" + username + "/" + latitudee + "/" + longitude, Toast.LENGTH_SHORT).show();
                        Request request = new Request.Builder()
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/feed/"+ username+"/"+latitudee+"/"+longitude)
                                .addHeader("Authorization", token)
                                .get()
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
                                               // Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                                                modelFeedArrayList.clear();
                                                JSONArray array = new JSONArray(res);
                                                System.out.println(res);
                                                JSONObject eachpostobj = null;
                                                JSONArray centerpoint = null;
                                                JSONObject eachcenterpointobj = null;
                                                String latitude = null;
                                                String longitude = null;
                                                String data = null;
                                                String imageLink = null;
                                                JSONArray hashtagarray = null;
                                                String key = null;
                                                String radius = null;
                                                String timeEpoch = null;
                                                JSONObject jsonObject = null;
                                                String fusername = null;
                                                String ffirstname = null;
                                                String flastname = null;
                                                String femail = null;
                                                String fheadline = null;
                                                String faboutMe = null;
                                                String flocality = null;
                                                String fprofileimage = null;
                                                //Toast.makeText(activity, array.length(), Toast.LENGTH_SHORT).show();
                                                for (int i = 0; i < array.length(); i++) {
                                                    String hashtag="";
                                                    // String apikey ="MGEr70kEnpeuOSktjnDabqCu8RFP_Yje0N-QqkFdkoo";
                                                    // Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                                                    eachpostobj = array.getJSONObject(i);
                                                    //Toast.makeText(getActivity(), eachpostobj.toString(), Toast.LENGTH_SHORT).show();

                                                    data = eachpostobj.getString("data");
                                                    imageLink = eachpostobj.getString("imageLink");
                                                    hashtagarray = eachpostobj.getJSONArray("hashtags");
                                                    ArrayList<String> hashtags = new ArrayList<>();
                                                    for (int k = 0; k < hashtagarray.length(); k++) {
                                                        hashtag= hashtag +" "+ hashtagarray.getString(k);
                                                       // Toast.makeText(getActivity(), hashtag, Toast.LENGTH_SHORT).show();
                                                        hashtags.add(hashtag);
                                                    }
                                                   // String allhashtags = hashtags.toString().substring(1, hashtags.size()-1);
                                                   // key = eachpostobj.getString("_id");
                                                    radius = eachpostobj.getString("radius");
                                                    timeEpoch = eachpostobj.getString("timeEpoch");
                                                    jsonObject = eachpostobj.getJSONObject("user");
                                                    fusername = jsonObject.getString("username");
                                                    ffirstname = jsonObject.getString("fName");
                                                    flastname = jsonObject.getString("lName");
                                                    femail = jsonObject.getString("email");
                                                    fheadline = jsonObject.getString("headline");
                                                    faboutMe = jsonObject.getString("aboutMe");
                                                    flocality = jsonObject.getString("locality");
                                                    fprofileimage = jsonObject.getString("profilePicture");
                                                   // Toast.makeText(activity, fprofileimage, Toast.LENGTH_SHORT).show();
                                                    if(fprofileimage.equals("")){
                                                        fprofileimage="https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png";
                                                    }
                                                    if(imageLink.equals("")){
                                                        imageLink="https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png";

                                                    }
                                                    ModelFeed modelFeed = new ModelFeed(fusername, radius, data, imageLink, fprofileimage, hashtag, token, key);
                                                    modelFeedArrayList.add(modelFeed);
                                                    feedAdapter.notifyDataSetChanged();


                                                    //Toast.makeText(activity, data, Toast.LENGTH_SHORT).show();

                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        thread.start();
                    }
                }
            });
        }
    }
    public void postrequest()
    {
        String urll = "https://asia-south1-milo-node.cloudfunctions.net/api/posts";

        jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, urll, postData, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = response;
                String key = "";
                try {
                    key = jsonObject.getString("_id");
                    if (key.equals("")) {
                        Toast.makeText(getActivity(), "Error Posting!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();

                }
                //  Toast.makeText(activity, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {


            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };

    }

    private void locationEnabled () {
        Dashboard activity = (Dashboard) getActivity();
        username = activity.getUsername();
        LocationManager lm = (LocationManager)
                activity.getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(activity)
                    .setMessage( "GPS Enable" )
                    .setCancelable(false)
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(activity, "Please enable location to see posts!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show() ;
        }
        else return;
    }


}