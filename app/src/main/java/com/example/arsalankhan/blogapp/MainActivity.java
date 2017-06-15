package com.example.arsalankhan.blogapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycleView;
    private DatabaseReference databaseReference;
    private ArrayList<Blog> arrayListBlog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");

        //sync the data in local database
        databaseReference.keepSynced(true);

        mRecycleView= (RecyclerView) findViewById(R.id.recycleView);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        arrayListBlog=new ArrayList<>();

        //retrieving data from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrayListBlog.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Blog blog= snapshot.getValue(Blog.class);

                    arrayListBlog.add(blog);
                }

                MyAdapter adapter=new MyAdapter(MainActivity.this,arrayListBlog);
                mRecycleView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    // for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.addpost){
            startActivity(new Intent(this,postActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
