package com.example.milo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    Context context;
    Uri uri;
    Bitmap bitmapImage;

    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();

    public FeedAdapter(Context context,ArrayList<ModelFeed> modelFeedArrayList){
        this.context = context;
        this.modelFeedArrayList = modelFeedArrayList;

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
                bundle.putString("fromwhichfragment", "feed");
                bundle.putString("username", modelFeed.getUsername());
                bundle.putString("description", modelFeed.getDescription());
                bundle.putString("radius", modelFeed.getTime());
                bundle.putString("hashtag", modelFeed.getHashtags());
                bundle.putString("profilepic", modelFeed.getComments());
                bundle.putString("postpic", modelFeed.getPostPic());
                bundle.putString("token", modelFeed.token);
                bundle.putString("key", modelFeed.key);
                Fragment fragment = new ViewPostsFragment();
                FragmentManager fragmentManager = ((Dashboard)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                ViewPostsFragment optionsFrag = new ViewPostsFragment ();
               // ((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, optionsFrag).addToBackStack(null).commit();
                fragment.setArguments(bundle);

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
