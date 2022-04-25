package com.example.milo;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class UserProfile extends Fragment {
    String token;
    String res;
    TextView pro_username,pro_fullname,pro_email,pro_headline,pro_aboutMe,pro_locality;
    ImageView pro_dp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Dashboard activity = (Dashboard) getActivity();
        String username = getArguments().getString("post_username");
         token = getArguments().getString("post_token");
         pro_username=view.findViewById(R.id.profile_username);
         pro_fullname=view.findViewById(R.id.profile_fullname);
         pro_email=view.findViewById(R.id.profile_email);
         pro_headline=view.findViewById(R.id.profile_headline);
         pro_aboutMe=view.findViewById(R.id.profile_aboutMe);
         pro_locality=view.findViewById(R.id.profile_Locality);
         pro_dp=view.findViewById(R.id.user_dp);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://asia-south1-milo-node.cloudfunctions.net/api/profile/"+ username)
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

                            //Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject object = new JSONObject(res);
                                pro_username.setText(object.getString("username"));
                                pro_fullname.setText(object.getString("fName")+" "+object.getString("lName"));
                                pro_headline.setText(object.getString("headline"));
                                pro_email.setText(object.getString("email"));
                                pro_aboutMe.setText(object.getString("aboutMe"));
                                pro_locality.setText(object.getString("locality"));
                                if(object.getString("profilePicture").equals("")){
                                    Picasso.with(getActivity()).load("https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png").into(pro_dp);
                                }
                                else{
                                    Picasso.with(getActivity()).load(object.getString("profilePicture")).into(pro_dp);

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

        return view;
    }
}