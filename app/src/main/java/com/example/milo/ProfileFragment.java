package com.example.milo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    ImageView settingsbutton;

    TextView trial;
    JSONObject postData;
    String res;
    RecyclerView recyclerView;
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    FeedAdapter feedAdapter;
    TextView seekbartext, addhashtag;
    SeekBar seekbar;
    TextView usernametext, fullnametext, headlinetext, aboutmetext;
    TextInputLayout hashtagbox;
    String token, latitudee, longitude;
    ImageView profileimage;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Dashboard activity = (Dashboard) getActivity();
        username = activity.getUsername();
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        if(savedInstanceState==null) {

            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPostsFragment()).commit();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        token = activity.getToken();
        loadingBar = new ProgressDialog(activity);
        loadingBar.setTitle("Please Wait");
        loadingBar.setMessage("Loading!");
        headlinetext=view.findViewById(R.id.post_que_user_name);
        fullnametext=view.findViewById(R.id.profile_name);
        usernametext=view.findViewById(R.id.profile_username);
        aboutmetext=view.findViewById(R.id.profile_aboutMe);
        profileimage=view.findViewById(R.id.user_dp);
        fetchLastLocation();
        settingsbutton=view.findViewById(R.id.settingsbutton);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }
    Dashboard activity = (Dashboard) getActivity();
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.bhome:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyPostsFragment()).commit();
                    break;
                case R.id.bevents:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyEventsFragment()).commit();
                    break;
                case R.id.bblurb:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyBlurbsFragment()).commit();
                    break;
            }
            return true;
        }
    };
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
                                .url("https://milo-backend.deta.dev/api/profile/"+username)
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
                                            JSONObject jsonObject= null;
                                            try {
                                                jsonObject = new JSONObject(res);
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String fusername = null;
                                            try {
                                                fusername = jsonObject.getString("username");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String ffirstname="";
                                            try {
                                              ffirstname= jsonObject.getString("fName");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String flastname="";
                                            try {
                                                flastname = jsonObject.getString("lName");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            try {
                                                String femail = jsonObject.getString("email");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String fheadline = null;
                                            try {
                                                fheadline = jsonObject.getString("headline");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String faboutMe = null;
                                            try {
                                                faboutMe = jsonObject.getString("aboutMe");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String flocality = null;
                                            try {
                                                flocality = jsonObject.getString("locality");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            String fprofileimage = null;
                                            try {
                                                fprofileimage = jsonObject.getString("profilePicture");
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                            assert fprofileimage != null;
                                            if(!fprofileimage.equals(""))
                                            {
                                                String finalFprofileimage = fprofileimage;
                                                Thread thread1 = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            URL url = new URL(finalFprofileimage);
                                                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    profileimage.setImageBitmap(image);
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
                                            fullnametext.setText(ffirstname+" "+flastname);
                                            usernametext.setText(fusername);
                                            headlinetext.setText(fheadline);
                                            aboutmetext.setText(faboutMe);
                                            loadingBar.dismiss();
                                            loadingBar.dismiss();
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
}