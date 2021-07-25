package com.example.milo;

import android.content.Context;
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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelEvent> modelEventArrayList = new ArrayList<>();

    public EventAdapter(Context context,ArrayList<ModelEvent> modelEventArrayList){
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
    public void onBindViewHolder(@NonNull @NotNull EventAdapter.MyViewHolder holder, int position) {
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
                bundle.putString("fromwhichfragment","feed");
                bundle.putString("username", modelEvent.getUsername());
                bundle.putString("description", modelEvent.getDescription());
                bundle.putString("radius", modelEvent.getRadius());
                bundle.putString("profilepic", modelEvent.getUserPic());
                bundle.putString("postpic", modelEvent.getEventPic());
                bundle.putString("token", modelEvent.token);
                bundle.putString("title", modelEvent.getTitle());
                bundle.putString("date", modelEvent.getTitle());
                bundle.putString("key", modelEvent.key);
                Fragment fragment = new ViewEventsFragment();
                FragmentManager fragmentManager = ((Dashboard)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, fragment);
                fragmentTransaction.commit();
                ViewEventsFragment optionsFrag = new ViewEventsFragment();
               // ((Dashboard)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, optionsFrag).addToBackStack(null).commit();
                fragment.setArguments(bundle);
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
            mView=itemView;
            date = itemView.findViewById(R.id.event_date);
            radius = itemView.findViewById(R.id.post_time);
            userImg = itemView.findViewById(R.id.post_que_user_image);
            eventImg = itemView.findViewById(R.id.postimage);

        }
    }
}
