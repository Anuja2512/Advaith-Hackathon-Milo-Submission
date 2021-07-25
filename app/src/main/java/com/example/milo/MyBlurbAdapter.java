package com.example.milo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyBlurbAdapter extends RecyclerView.Adapter<MyBlurbAdapter.MyViewHolder>{
    Context context;
    ArrayList<ModelBlurb> modelBlurbArrayList = new ArrayList<>();
    Dialog dialog;
    public MyBlurbAdapter(Context context,ArrayList<ModelBlurb> modelBlurbArrayList){
        this.context=context;
        this.modelBlurbArrayList = modelBlurbArrayList;

    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blurbitem, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final ModelBlurb modelBlurb = modelBlurbArrayList.get(position);
        holder.tv_username.setText(modelBlurb.getUsername());
        holder.tv_hashtags.setText(modelBlurb.getHashtags());
        holder.tv_radius.setText(modelBlurb.getRadius());
        holder.tv_title.setText(modelBlurb.getTitle());
        holder.tv_profession.setText(modelBlurb.getProfession());
        Picasso.with(context).load(modelBlurb.getUserPic()).into(holder.iv_imgDp);
        Picasso.with(context).load(modelBlurb.getBlurbPic()).into(holder.iv_imgBlurb);
        holder.iv_imgBlurb.setVisibility(View.VISIBLE);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromwhichfragment", "own");
                bundle.putString("username", modelBlurb.getUsername());
                bundle.putString("description", modelBlurb.getDescription());
                bundle.putString("radius", modelBlurb.getRadius());
                bundle.putString("hashtag", modelBlurb.getHashtags());
                bundle.putString("profilepic", modelBlurb.getUserPic());
                bundle.putString("postpic", modelBlurb.getBlurbPic());
                bundle.putString("token", modelBlurb.token);
                bundle.putString("key", modelBlurb.key);
                ViewBlurbFragment optionsFrag = new ViewBlurbFragment ();
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
                TextView seekbartext = dialog.findViewById(R.id.seekbartext);
                seekbartext.setText(modelBlurb.getRadius()+" km");
                seekBar.setProgress(Integer.parseInt(modelBlurb.getRadius()));
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
                                .url("https://milo-backend.deta.dev/api/promotion/"+ modelBlurb.key)
                                .addHeader("Authorization", modelBlurb.token)
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
                                                modelBlurbArrayList.remove(position);

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
                                .url("https://milo-backend.deta.dev/api/updatepromotion/"+ modelBlurb.key)
                                .addHeader("Authorization", modelBlurb.token)
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
                                            holder.tv_radius.setText(radius + " km");
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
        return modelBlurbArrayList.size();
    }
    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView tv_username,tv_hashtags,tv_radius,tv_title,tv_profession;
        ImageView iv_imgDp,iv_imgBlurb;
        View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            tv_username=itemView.findViewById(R.id.post_que_user_name);
            tv_hashtags=itemView.findViewById(R.id.text_que_time_dis);
            tv_radius=itemView.findViewById(R.id.post_time);
            tv_title=itemView.findViewById(R.id.blurb_title);
            tv_profession=itemView.findViewById(R.id.blurb_profession);
            iv_imgDp=itemView.findViewById(R.id.post_que_user_image);
            iv_imgBlurb=itemView.findViewById(R.id.postimage);
            mView=itemView;

        }
    }
}
