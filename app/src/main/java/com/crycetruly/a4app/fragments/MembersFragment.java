package com.crycetruly.a4app.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crycetruly.a4app.R;
import com.crycetruly.a4app.models.User;
import com.crycetruly.a4app.services.FirebaseMessaging;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    FirebaseRecyclerAdapter<User, FriendsViewHolder> adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mUserDatabase;
    private RecyclerView mUserFriendsList;
    private String currentUser;
    private static final String TAG = "MembersFragment";
    private Query query;
    private ProgressBar progressBar;


    public MembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mainView = inflater.inflate(R.layout.fragment_members, container, false);

        progressBar = mainView.findViewById(R.id.progress2);

        //------------------------FIREBASE-----------------------------
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser);
        query = mDatabase.orderByChild("online");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        mUserFriendsList = mainView.findViewById(R.id.friendList);
        mUserFriendsList.setHasFixedSize(true);
        mUserFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    TextView friendtest = mainView.findViewById(R.id.nofriends);
                    friendtest.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Inflate the layout for this fragment
        return mainView;


    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.singlemember, parent, false);

                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsViewHolder viewHolder, int position, final User model) {
                final String userId = getRef(position).getKey();
                mUserDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    viewHolder.setName(dataSnapshot.child("phone").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("This will remove this user from your circle ,Are you sure you want to proceed?");
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(), "Removing user from circle", Toast.LENGTH_SHORT).show();
//mProfileSendReqBtn.setText("UNFRIENDING THIS PERSON");
                                Log.d(TAG, "onClick: We were friends before");
                                Map<String, Object> unfriendMap = new HashMap<>();
                                unfriendMap.put("Friends/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + userId, null);


                                FirebaseDatabase.getInstance().getReference().updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                        if (databaseError == null) {
com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic(userId);
                                            Toast.makeText(getContext(), "User removed successfully", Toast.LENGTH_SHORT).show();

                                        } else {

                                            String error = databaseError.getMessage();

                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        }).show();
                    }
                });

            }

        };

        mUserFriendsList.setAdapter(adapter);
        adapter.startListening();
        super.onStart();

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CircleImageView dp;
        ImageView delete;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            delete = mView.findViewById(R.id.delete);
        }

        public void setName(String name) {
            TextView nameTextView = mView.findViewById(R.id.name);
            nameTextView.setText(name);
        }


        public void setUserOnline(String online_status) {
            ImageView userOnline = mView.findViewById(R.id.online_status);
            if (online_status.equals("true")) {
                userOnline.setVisibility(View.VISIBLE);

            } else {
                userOnline.setVisibility(View.INVISIBLE);

            }

        }


    }
}
