package com.example.milo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class ViewBlurbFragment extends Fragment {
    TextView usernametext, hashtagtext, radiustext, descriptiontext;
    ImageView img_proPic, img_postPic;
    String token, latitudee, longitude, key;
    LinearLayout layout;
    ImageView postcomment;
    String frame;
    EditText commenttext;
    RecyclerView recyclerView;
    ArrayList<ModelComment> modelFeedArrayList = new ArrayList<>();
    FeedAdapter feedAdapter;
    CommentAdapter commentadapter;
    private GoogleMap mMap;
    Button btn;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
    RequestQueue requestQueue;
    Geocoder geocoder;
    private ProgressDialog loadingBar;
    RatingBar rt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_blurb, container, false);
        Dashboard activity = (Dashboard) getActivity();
        String myusername = activity.getUsername();
        String username = getArguments().getString("username");
        String hasntags = getArguments().getString("hashtag");
        String radius = getArguments().getString("radius");
        String description = getArguments().getString("description");
        String postpic = getArguments().getString("postpic");
        frame = getArguments().getString("fromwhichfragment");
        String profilepic = getArguments().getString("profilepic");
        token = getArguments().getString("token");
        key = getArguments().getString("key");
        usernametext=view.findViewById(R.id.post_que_user_name);
        loadingBar = new ProgressDialog(activity);
        loadingBar.setTitle("Please Wait");
        loadingBar.setMessage("Loading!");
        rt = view.findViewById(R.id.ratingBar);


        //finding the specific RatingBar with its unique ID

        //Use for changing the color of RatingBar
        //stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        recyclerView = view.findViewById(R.id.commentRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        commentadapter = new CommentAdapter(getActivity(), modelFeedArrayList);
        recyclerView.setAdapter(commentadapter);
        geocoder = new Geocoder(getActivity());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        fetchLastLocation();

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        modelFeedArrayList.clear();
        layout = view.findViewById(R.id.profilelayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("post_username", username);
                bundle.putString("post_token", token);
                UserProfile userProfile = new UserProfile();
                if(getArguments().getString("fromwhichfragment").equals("own"))
                {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userProfile).commit();

                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, userProfile).commit();
                }
                userProfile.setArguments(bundle);
            }
        });
        usernametext.setText(username);
        hashtagtext=view.findViewById(R.id.text_que_time_dis);
        hashtagtext.setText(hasntags);
        radiustext=view.findViewById(R.id.post_time);
        radiustext.setText(radius);
        descriptiontext=view.findViewById(R.id.user_que);
        descriptiontext.setText(description);
        img_proPic=view.findViewById(R.id.post_que_user_image);
        img_postPic=view.findViewById(R.id.postimage);
        Picasso.with(getActivity()).load(profilepic).into(img_proPic);
        if(!postpic.equals("https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png"))
        {
            img_postPic.setVisibility(View.VISIBLE);
        }
        commenttext=view.findViewById(R.id.commenttext);
        Picasso.with(getActivity()).load(postpic).into(img_postPic);
        postcomment=view.findViewById(R.id.button2);
        rt.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int rate = (int)rating;
                switch (rate){
                    case 1:
                        //Toast.makeText(activity, "Pressed 1", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //Toast.makeText(activity, "Pressed 2", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //Toast.makeText(activity, "Pressed 3", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        //Toast.makeText(activity, "Pressed 4", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        //Toast.makeText(activity, "Pressed 5", Toast.LENGTH_SHORT).show();
                        break;



                }
            }
        });
        postcomment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Float rate = rt.getRating();
                if(commenttext.getText().toString().equals(""))
                {
                    commenttext.setError("No comment to post!");
                }
               else if(rate.toString().equals("0.0"))
                {
                    Toast.makeText(activity, "Please give a rating", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String comment = commenttext.getText().toString();
                    JSONObject postData = new JSONObject();
                    try {

                        Instant instant = Instant.now();
                        Long epochValue = instant.getEpochSecond();
                        postData.put("username",myusername);
                        postData.put("rate", rate.toString());
                        postData.put("promotionID", key);
                        postData.put("feedback", comment);
                        OkHttpClient client = new OkHttpClient();
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        // put your json here
                        RequestBody body = RequestBody.create(JSON, postData.toString());
                        Request request = new Request.Builder()
                                .url("https://milo-backend.deta.dev/api/ratings")
                                .addHeader("Authorization", token)
                                .post(body)
                                .build();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                    String resStr = response.body().string();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                               // Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();

                                                if(comment.equals(""))
                                                {
                                                    Toast.makeText(getActivity(), "comment not posted!", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    commenttext.setText("");
                                                    fetchLastLocation();
                                                    Toast.makeText(getActivity(), "Comment Sent!", Toast.LENGTH_SHORT).show();
                                                }

                                            // Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        thread.start();
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }


                }
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
                        OkHttpClient client = new OkHttpClient();
                        //Toast.makeText(activity, latitudee + longitude, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, username, Toast.LENGTH_SHORT).show();
                        // Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, "https://milo-backend.deta.dev/api/feed/" + username + "/" + latitudee + "/" + longitude, Toast.LENGTH_SHORT).show();
                        Request request = new Request.Builder()
                                .url("https://milo-backend.deta.dev/api/promotion/"+key+"/"+latitudee+"/"+longitude)
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
                                                //Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                                                //System.out.println(res);
                                                modelFeedArrayList.clear();
                                                JSONObject object = new JSONObject(res);
                                                JSONObject eachpostobj = null;
                                                JSONObject jsonObject = null;
                                                String fusername = null;
                                                String fprofileimage = null;
                                                String comment=null;

                                                JSONArray array = object.getJSONArray("comments");
                                                //Toast.makeText(activity, array.length(), Toast.LENGTH_SHORT).show();
                                                for (int i = 0; i < array.length(); i++) {
                                                    eachpostobj = array.getJSONObject(i);
                                                    comment="Rating: " + eachpostobj.getString("rate")+"\n"+"Feedback: "+eachpostobj.getString("feedback");

                                                    jsonObject = eachpostobj.getJSONObject("user");
                                                    fusername = jsonObject.getString("username");
                                                    fprofileimage = jsonObject.getString("profilePicture");
                                                    // Toast.makeText(activity, fprofileimage, Toast.LENGTH_SHORT).show();
                                                    if(fprofileimage.equals("")){
                                                        fprofileimage="https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png";
                                                    }
                                                    ModelComment modelComment = new ModelComment(fprofileimage, fusername, comment, frame, token);
                                                    modelFeedArrayList.add(modelComment);
                                                    commentadapter.notifyDataSetChanged();


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

}