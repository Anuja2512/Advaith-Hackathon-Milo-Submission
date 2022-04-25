package com.example.milo;

import android.Manifest;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyEventsFragment extends Fragment {

    TextView trial;
    JSONObject postData;
    String res;
    RecyclerView recyclerView;
    ArrayList<ModelEvent> modelEventArrayList = new ArrayList<>();
    MyEventsAdapter myEventsAdapter;
    ImageView calendarbutton;
    TextView seekbartext, addhashtag;
    SeekBar seekbar;
    EditText postcontenttext, hashtagtext, titletext;
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
    AlertDialog.Builder builder;
    private PendingIntent pendingIntent;
    private MaterialTimePicker timePicker;
    private AlarmManager alarmManager;
    public MaterialDatePicker datePicker;
    long today;
    String dateOfEvent;
    static Calendar calendar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        Dashboard activity = (Dashboard) getActivity();
        username = activity.getUsername();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        token = activity.getToken();
        loadingBar = new ProgressDialog(activity);
        loadingBar.setTitle("Please Wait");
        loadingBar.setMessage("Loading!");
        recyclerView = view.findViewById(R.id.EventRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        myEventsAdapter = new MyEventsAdapter(getActivity(), modelEventArrayList);
        recyclerView.setAdapter(myEventsAdapter);
        modelEventArrayList.clear();

        geocoder = new Geocoder(getActivity());
        fetchLastLocation();
        return view;
    }

    private void fetchLastLocation() {
        Dashboard activity = (Dashboard) getActivity();
        loadingBar.show();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Please turn your location on!", Toast.LENGTH_SHORT).show();

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
                        Request request = new Request.Builder()
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/userevents/"+ username)
                                .addHeader("Authorization", token)
                                .get()
                                .build();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Response response = client.newCall(request).execute();
                                    res = response.body().string();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //
                                            try {
                                               // Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                                // Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                                                modelEventArrayList.clear();
                                                JSONArray array = new JSONArray(res);
                                                //System.out.println(res);
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
                                                String deadlineTimeEpoch = null;
                                                JSONObject jsonObject = null;
                                                String fusername = null;
                                                String ffirstname = null;
                                                String flastname = null;
                                                String femail = null;
                                                String fheadline = null;
                                                String faboutMe = null;
                                                String flocality = null;
                                                String title=null;
                                                String fprofileimage = null;
                                                //Toast.makeText(activity, array.length(), Toast.LENGTH_SHORT).show();
                                                for (int i = 0; i < array.length(); i++) {
                                                    String hashtag="";
                                                    System.out.println(res);
                                                    // String apikey ="MGEr70kEnpeuOSktjnDabqCu8RFP_Yje0N-QqkFdkoo";
                                                    // Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                                                    eachpostobj = array.getJSONObject(i);
                                                    //Toast.makeText(getActivity(), eachpostobj.toString(), Toast.LENGTH_SHORT).show();
                                                    imageLink = eachpostobj.getString("imageLink");
                                                    // String allhashtags = hashtags.toString().substring(1, hashtags.size()-1);
                                                    key = eachpostobj.getString("_id");
                                                    radius = eachpostobj.getString("radius");
                                                    deadlineTimeEpoch = eachpostobj.getString("deadlineTimeEpoch");
                                                    jsonObject = eachpostobj.getJSONObject("user");
                                                    fusername = jsonObject.getString("username");
                                                    ffirstname = jsonObject.getString("fName");
                                                    flastname = jsonObject.getString("lName");
                                                    femail = jsonObject.getString("email");
                                                    fheadline = jsonObject.getString("headline");
                                                    faboutMe = jsonObject.getString("aboutMe");
                                                    flocality = jsonObject.getString("locality");
                                                    title = eachpostobj.getString("title");
                                                    data = eachpostobj.getString("description");
                                                    Long epochlong = Long.parseLong(deadlineTimeEpoch);
                                                    Date d = new Date( epochlong * 1000);

                                                    fprofileimage = jsonObject.getString("profilePicture");
                                                    if(fprofileimage.equals("")){
                                                        fprofileimage="https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png";
                                                    }
                                                    if(imageLink.equals("")){
                                                        imageLink="https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png";

                                                    }
                                                    String thedate = String.format("%tc", d);
                                                    ModelEvent modelEvent = new ModelEvent(fusername, title,radius,data, thedate.substring(0,10) , imageLink, fprofileimage, token, key);
                                                    modelEventArrayList.add(modelEvent);
                                                    myEventsAdapter.notifyDataSetChanged();

                                                    // Toast.makeText(activity, data, Toast.LENGTH_SHORT).show();

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

}