package com.example.arsalankhan.blogapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arsalan khan on 6/15/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Blog> arrayListBlog=new ArrayList<>();

    public MyAdapter(Context context, ArrayList<Blog> arrayList){
        this.context=context;
        arrayListBlog=arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.single_row,parent,false);
        MyViewHolder holder=new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Blog blog=arrayListBlog.get(position);

        holder.textViewTitle.setText(blog.getTitle());
        holder.textViewDescription.setText(blog.getDescription());
        holder.textViewUserName.setText("Post by: "+blog.getUserName());
        

        Picasso.with(context).load(blog.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
              // When onSuccess execute this means data fetch from local database
                holder.progressDialog.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

                //when onError execute that's means no data in local database
                // so we fetch data from Firebase database

                Picasso.with(context).load(blog.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressDialog.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListBlog.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewTitle,textViewDescription,textViewUserName;
        View progressDialog;
        public MyViewHolder(View itemView) {
            super(itemView);

            imageView= (ImageView) itemView.findViewById(R.id.postImage);
            textViewTitle= (TextView) itemView.findViewById(R.id.TextViewBlogTitle);
            textViewDescription= (TextView) itemView.findViewById(R.id.TextViewblogDescription);
            textViewUserName= (TextView) itemView.findViewById(R.id.textViewUserName);
            progressDialog=itemView.findViewById(R.id.progressDialog);
        }
    }
}


