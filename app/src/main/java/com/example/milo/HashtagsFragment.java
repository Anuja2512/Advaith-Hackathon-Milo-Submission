package com.example.milo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagsFragment extends Fragment {
    String token, latitudee, longitude, username;
    Button updatehashatg;
    Location currentLocation;
    FeedAdapter feedAdapter;
    EditText hashtagtext;
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
    RequestQueue requestQueue;
    Geocoder geocoder;
    private ProgressDialog loadingBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_hashtags, container, false);
        Dashboard activity = (Dashboard) getActivity();
        username = activity.getUsername();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        fetchLastLocation();
        token = activity.getToken();
        loadingBar = new ProgressDialog(activity);
        recyclerView = view.findViewById(R.id.HashtagRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        feedAdapter = new FeedAdapter(getActivity(), modelFeedArrayList);
        recyclerView.setAdapter(feedAdapter);

        loadingBar.setTitle("Please Wait");
        loadingBar.setMessage("Loading!");
        updatehashatg = view.findViewById(R.id.updatehashtags);
        hashtagtext = view.findViewById(R.id.hashtagedittext);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
              .url("https://milo-backend.deta.dev/api/hashtag/"+username)
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
                               JSONArray array = jsonObject.getJSONArray("value");
                               StringBuilder hashtags= new StringBuilder();
                               for(int i=0;i< array.length();i++){
                                   //Toast.makeText(getActivity(), array.getString(i), Toast.LENGTH_SHORT).show();
                                   String hasshhh = " "+array.getString(i);
                                   hashtags.append(hasshhh);
                               }
                               hashtagtext.setText(hashtags.toString());


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
       // Toast.makeText(activity, latitudee+longitude, Toast.LENGTH_SHORT).show();

        updatehashatg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hashtags = hashtagtext.getText().toString();
                    Pattern p = Pattern.compile("#\\S+");
                    List<String> hashTags = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray();
                    Matcher matcher = p.matcher(hashtagtext.getText());
                    while (matcher.find()) {
                        hashTags.add(matcher.group(0));
                        jsonArray.put(matcher.group(0).toLowerCase());
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("value", jsonArray);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    // put your json here
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                    Request request = new Request.Builder()
                            .url("https://milo-backend.deta.dev/api/hashtag/"+username)
                            .addHeader("Authorization", token)
                            .put(body)
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

                                            fetchLastLocation();
                                            Toast.makeText(getActivity(), "Hashtags updated", Toast.LENGTH_SHORT).show();

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

        });

        return view;
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
                        OkHttpClient client1 = new OkHttpClient();
                        Request request1 = new Request.Builder()
                                .url("https://milo-backend.deta.dev/api/hashfeed/"+username+"/"+latitudee+"/"+longitude)
                                .get()
                                .addHeader("Authorization", token)
                                .build();
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Response response = client1.newCall(request1).execute();
                                    String res = response.body().string();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //
                                            try {
                                                try {
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
                                                        centerpoint = eachpostobj.getJSONArray("center_point");
                                                        for (int j = 0; j < centerpoint.length(); j++) {
                                                            eachcenterpointobj = centerpoint.getJSONObject(j);
                                                            latitude = eachcenterpointobj.getString("lat");
                                                            longitude = eachcenterpointobj.getString("lng");
                                                        }
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
                                                        key = eachpostobj.getString("key");
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
                        thread1.start();

                    }
                }
            });
        }
    }
}