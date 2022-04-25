package com.example.milo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MyPostsAdapter  extends RecyclerView.Adapter<MyPostsAdapter.MyViewHolder> {
    Context context;
    Uri uri;
    Bitmap bitmapImage;
    Dialog dialog;
    public  String ischanged="";
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    public MyPostsAdapter(Context context,ArrayList<ModelFeed> modelFeedArrayList){
        this.context = context;
        this.modelFeedArrayList = modelFeedArrayList;

    }

    public MyPostsAdapter() {
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeditem, parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final ModelFeed modelFeed = modelFeedArrayList.get(position);
        holder.tvUsername.setText(modelFeed.getUsername());
        holder.tvStatus.setText(modelFeed.getDescription());
        holder.tvTime.setText(modelFeed.getTime()+" km");
        holder.tvhashtag.setText(modelFeed.getHashtags());
        Picasso.with(context).load(modelFeed.getComments()).into(holder.img_proPic);
        Picasso.with(context).load(modelFeed.getPostPic()).into(holder.img_postPic);
        if(!modelFeed.getPostPic().equals("https://www.seekpng.com/png/detail/41-410093_circled-user-icon-user-profile-icon-png.png"))
        {
            holder.img_postPic.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromwhichfragment", "own");
                bundle.putString("username", modelFeed.getUsername());
                bundle.putString("description", modelFeed.getDescription());
                bundle.putString("radius", modelFeed.getTime());
                bundle.putString("hashtag", modelFeed.getHashtags());
                bundle.putString("profilepic", modelFeed.getComments());
                bundle.putString("postpic", modelFeed.getPostPic());
                bundle.putString("token", modelFeed.token);
                bundle.putString("key", modelFeed.key);
                Toast.makeText(view.getContext(), "Open the Post", Toast.LENGTH_SHORT).show();
                ViewPostsFragment optionsFrag = new ViewPostsFragment ();
                ((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, optionsFrag).addToBackStack(null).commit();
                optionsFrag.setArguments(bundle);
            }
        });


        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                 //Toast.makeText(view.getContext(), "Long Click Successful", Toast.LENGTH_SHORT).show();
                CharSequence options[] = new CharSequence[]{
                        "Delete Post",
                        "Update Radius",
                        "Cancel"
                };
                dialog=new Dialog(context);
                dialog.setContentView(R.layout.help_dialogue);
                dialog.getWindow().setLayout(1000, 1200);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                SeekBar seekBar = dialog.findViewById(R.id.seekBar);
                seekBar.setProgress(Integer.parseInt(modelFeed.getTime()));
                TextView seekbartext = dialog.findViewById(R.id.seekbartext);
                seekbartext.setText(modelFeed.getTime()+" km");
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int radius, boolean b) {
                        seekbartext.setText(radius + " km");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                Button delete = dialog.findViewById(R.id.deletebtn);
                Button update = dialog.findViewById(R.id.updatebutton);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/post/"+ modelFeed.key)
                                .addHeader("Authorization", modelFeed.token)
                                .delete()
                                .build();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    assert response != null;
                                    String res = response.body().string();
                                    JSONObject jsonObject = new JSONObject(res);
                                    String message = jsonObject.getString("message");
                                    if(message.equals("Deleted Successfully."))
                                    {
                                        ischanged="changed";
                                        dialog.dismiss();

                                        Activity activity = (Dashboard)context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                modelFeedArrayList.remove(position);

                                                notifyItemRemoved(position);
                                            }
                                        });


                                    }
                                    if(message.equals("Post Does not Exist"))
                                    {
                                        dialog.dismiss();
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        thread.start();

                    }
                });
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Integer rad =  seekBar.getProgress();
                       String radius = rad.toString();
                        OkHttpClient client = new OkHttpClient();
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("radius", radius);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        // put your json here
                        RequestBody body = RequestBody.create(JSON, postData.toString());
                        Request request = new Request.Builder()
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/updatepost/"+ modelFeed.key)
                                .addHeader("Authorization", modelFeed.token)
                                .put(body)
                                .build();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    assert response != null;
                                    String res = response.body().string();
                                    JSONObject jsonObject = new JSONObject(res);
                                   // String message = jsonObject.getString("message");
                                    Activity activity = (Dashboard)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.tvTime.setText(radius + " km");
                                        }
                                    });
                                    dialog.dismiss();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        thread.start();


                    }
                });
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelFeedArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername,tvTime,tvStatus, tvhashtag;
        ImageView img_proPic, img_postPic;
         View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            img_proPic= itemView.findViewById(R.id.post_que_user_image);
            img_postPic=itemView.findViewById(R.id.postimage);
            tvUsername=itemView.findViewById(R.id.post_que_user_name);
            tvTime=itemView.findViewById(R.id.post_time);
            tvStatus=itemView.findViewById(R.id.user_que);
            tvhashtag=itemView.findViewById(R.id.text_que_time_dis);
            mView=itemView;
        }
    }
}
