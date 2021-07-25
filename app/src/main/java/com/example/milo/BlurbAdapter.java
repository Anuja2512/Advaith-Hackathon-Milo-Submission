package com.example.milo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BlurbAdapter extends RecyclerView.Adapter<BlurbAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelBlurb> modelBlurbArrayList = new ArrayList<>();

    public BlurbAdapter(Context context,ArrayList<ModelBlurb> modelBlurbArrayList){
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
    public void onBindViewHolder(@NonNull @NotNull BlurbAdapter.MyViewHolder holder, int position) {
        final ModelBlurb modelBlurb = modelBlurbArrayList.get(position);
        holder.tv_username.setText(modelBlurb.getUsername());
        holder.tv_hashtags.setText(modelBlurb.getHashtags());
        holder.tv_radius.setText(modelBlurb.getRadius());
        holder.tv_title.setText(modelBlurb.getTitle());
        holder.tv_profession.setText(modelBlurb.getProfession());
        Picasso.with(context).load(modelBlurb.getUserPic()).into(holder.iv_imgDp);
        Picasso.with(context).load(modelBlurb.getBlurbPic()).into(holder.iv_imgBlurb);
        holder.iv_imgBlurb.setVisibility(View.VISIBLE);
        Fragment fragment = new ViewBlurbFragment();
        FragmentManager fragmentManager = ((Dashboard)context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, fragment);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromwhichfragment", "feed");
                bundle.putString("username", modelBlurb.getUsername());
                bundle.putString("description", modelBlurb.getDescription());
                bundle.putString("radius", modelBlurb.getRadius());
                bundle.putString("hashtag", modelBlurb.getHashtags());
                bundle.putString("profilepic", modelBlurb.getUserPic());
                bundle.putString("postpic", modelBlurb.getBlurbPic());
                bundle.putString("token", modelBlurb.token);
                bundle.putString("key", modelBlurb.key);
                fragmentTransaction.commit();
                //ViewBlurbFragment optionsFrag = new ViewBlurbFragment ();
                //((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, optionsFrag).addToBackStack(null).commit();
                fragment.setArguments(bundle);
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
