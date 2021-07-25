package com.example.milo;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    Context context;
    ArrayList<ModelComment> modelCommentArrayList = new ArrayList<>();

    public CommentAdapter(Context context,ArrayList<ModelComment> modelCommentArrayList){
        this.context=context;
        this.modelCommentArrayList=modelCommentArrayList;

    }

    @NonNull
    @NotNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentitem, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.MyViewHolder holder, int position) {
        final ModelComment modelComment = modelCommentArrayList.get(position);
        holder.adpt_username.setText(modelComment.getCmt_username());
        holder.adpt_comment.setText(modelComment.getComment());
        Picasso.with(context).load(modelComment.getCmt_userPic()).into(holder.adpt_proPic);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, modelComment.getFrame(), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("post_username", modelComment.getCmt_username());
                bundle.putString("post_token", modelComment.getToken());
                UserProfile userProfile = new UserProfile();
                if(modelComment.getFrame().equals("own"))
                {
                    ((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userProfile).commit();

                }
                else  {
                    ((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, userProfile).commit();
                }
                userProfile.setArguments(bundle);

            }
        });


    }

    @Override
    public int getItemCount() {
        return modelCommentArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView adpt_username,adpt_comment;
        ImageView adpt_proPic;
        LinearLayout layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            adpt_username = itemView.findViewById(R.id.usernametextview);
            adpt_comment = itemView.findViewById(R.id.commenttextview);
            adpt_proPic = itemView.findViewById(R.id.profileimagecomment);
            layout = itemView.findViewById(R.id.layout);

        }
    }
}
