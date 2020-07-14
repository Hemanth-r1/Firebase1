package com.example.firebase.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase.Adapter.MyPhotoAdapter;
import com.example.firebase.Modle.User;
import com.example.firebase.Modle.post;
import com.example.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountFragment extends Fragment {

    ImageView image_profile, options;
    TextView posts, followers, following, fullname ,bio, username;
    Button edit_profile;

    private List<String> mySaved;


    RecyclerView recyclerView_saved;
    MyPhotoAdapter myPhotoAdapter_saved;
    List<post> postList_saved;


    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<post> postList;

    FirebaseUser firebaseUser;
    String profileid;
    ImageButton my_photos, saved_photos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.post);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        bio = view.findViewById(R.id.bio);
        my_photos = view.findViewById(R.id.my_photos);
        saved_photos = view.findViewById(R.id.saved_photos);
        image_profile = view.findViewById(R.id.image_profile);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);


        recyclerView_saved = view.findViewById(R.id.recycler_view_saved);
        recyclerView_saved.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saved = new GridLayoutManager(getContext(), 3);
        recyclerView_saved.setLayoutManager(linearLayoutManager_saved);
        postList_saved = new ArrayList<>();
        myPhotoAdapter_saved = new MyPhotoAdapter(getContext(), postList_saved);
        recyclerView_saved.setAdapter(myPhotoAdapter_saved);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saved.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myPhotos();
        mysaved();

        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");

        }else {
            checkFollow();
            saved_photos.setVisibility(View.GONE);
        }
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){
                    //go to edit profile
                }else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("Following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileid).child("Followers").child(firebaseUser.getUid()).setValue(true);

                }else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("Following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileid).child("Followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saved.setVisibility(View.GONE);
            }
        });

        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saved.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext()== null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                }else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    post post = snapshot1.getValue(post.class);

                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                  posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                //    post post = snapshot1.getPublisher().equals(profileid);
               //     if (post.getPublisher().equals(profileid)){
                //        postList.add(post);
                    }
                }

              // Collections.reverse(postList);
              //myPhotoAdapter.notifyDataSetChanged();

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

    private void mysaved(){


        mySaved = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("saved").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    mySaved.add(snapshot1.getKey());
                }
                readSaved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readSaved(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 :snapshot.getChildren()){
                    post post = snapshot1.getValue(post.class);

                    for (String id : mySaved){
                        if (post.getPostid().equals(id)){
                            postList_saved.add(post);
                        }
                    }
                }
                myPhotoAdapter_saved.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
}