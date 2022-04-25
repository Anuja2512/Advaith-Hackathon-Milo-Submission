package com.example.milo;

import android.content.Context;
import android.graphics.Color;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Dialog;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelEvent> modelEventArrayList = new ArrayList<>();
    Dialog dialog;

    public MyEventsAdapter(Context context,ArrayList<ModelEvent> modelEventArrayList){
        this.context=context;
        this.modelEventArrayList=modelEventArrayList;

    }


    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventitem, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyEventsAdapter.MyViewHolder holder, int position) {
        final ModelEvent modelEvent = modelEventArrayList.get(position);
        holder.username.setText(modelEvent.getUsername());
        holder.title.setText(modelEvent.getTitle());
        holder.radius.setText(modelEvent.getRadius());
        holder.date.setText(modelEvent.getDate());
        Picasso.with(context).load(modelEvent.getUserPic()).into(holder.userImg);
        Picasso.with(context).load(modelEvent.getEventPic()).into(holder.eventImg);
        holder.userImg.setVisibility(View.VISIBLE);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromwhichfragment","own");
                bundle.putString("username", modelEvent.getUsername());
                bundle.putString("description", modelEvent.getDescription());
                bundle.putString("radius", modelEvent.getRadius());
                bundle.putString("profilepic", modelEvent.getUserPic());
                bundle.putString("postpic", modelEvent.getEventPic());
                bundle.putString("token", modelEvent.token);
                bundle.putString("title", modelEvent.getTitle());
                bundle.putString("date", modelEvent.getTitle());
                bundle.putString("key", modelEvent.key);
                //Toast.makeText(view.getContext(), "Open the Post", Toast.LENGTH_SHORT).show();
                ViewEventsFragment optionsFrag = new ViewEventsFragment();
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
                seekBar.setProgress(Integer.parseInt(modelEvent.getRadius()));
                TextView seekbartext = dialog.findViewById(R.id.seekbartext);
                seekbartext.setText(modelEvent.getRadius()+" km");
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
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/event/"+ modelEvent.key)
                                .addHeader("Authorization", modelEvent.token)
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
                                        dialog.dismiss();
                                        Activity activity = (Dashboard)context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                modelEventArrayList.remove(position);

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
                                .url("https://asia-south1-milo-node.cloudfunctions.net/api/updateevent/"+ modelEvent.key)
                                .addHeader("Authorization", modelEvent.token)
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

                                    assert response != null;
                                    Activity activity = (Dashboard)context;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.radius.setText(radius + " km");
                                        }
                                    });
                                    dialog.dismiss();


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
        return modelEventArrayList.size();
    }
    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView username,title,date,radius;
        ImageView userImg,eventImg;
        View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.post_que_user_name);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            radius = itemView.findViewById(R.id.post_time);
            userImg = itemView.findViewById(R.id.post_que_user_image);
            eventImg = itemView.findViewById(R.id.postimage);
            mView=itemView;

        }
    }
}
