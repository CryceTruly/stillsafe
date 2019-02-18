package com.crycetruly.a4app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.crycetruly.a4app.models.Chat;
import com.crycetruly.a4app.utils.GetShortTimeAgo;
import com.crycetruly.a4app.utils.Handy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter adapter;
    TextView nomsgs;
    private Context mContext;
    private RecyclerView mUserFriendsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mUserDatabase, ChatsDb;
    private String currentUser;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "InboxActivity";
    GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chats);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setEnabled(false);

        mContext = getBaseContext();

        progressBar = findViewById(R.id.progress);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chat")
                .child(currentUser);
        mDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("counsellors");
        mUserDatabase.keepSynced(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Handy.isNetworkAvailable(getBaseContext())) {
                    onStart();
                } else {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }


                    Snackbar.make(progressBar, "Not connected", Snackbar.LENGTH_LONG).setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                        }
                    }).show();

                }

            }
        });
        //------------------------------RECYCLERVIEW    ------------------------//
        mUserFriendsList = findViewById(R.id.friendList);
        mUserFriendsList.setHasFixedSize(true);
        nomsgs = findViewById(R.id.themessos);
        mUserFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onStart() {
        try {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d(TAG, "chats" + dataSnapshot);
                    if (!dataSnapshot.exists()) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            nomsgs.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setEnabled(true);

                        } catch (NullPointerException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            nomsgs.setVisibility(View.INVISIBLE);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (NullPointerException e) {

        }
        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(mDatabase.orderByChild("fitnessNum"), Chat.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Chat, TrulysChatVH>(options) {
            @Override
            public TrulysChatVH onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.single_chat, parent, false);

                return new TrulysChatVH(view,mContext);
            }

            @Override
            protected void onBindViewHolder(final TrulysChatVH viewHolder, int position, Chat model) {
                final String userId = getRef(position).getKey();

                long time = model.getTimestamp();
                String times = GetShortTimeAgo.getTimeAgo(time, mContext);
                viewHolder.setSentDate(times);
viewHolder.setMessage(model.getMessage());

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showOptionsMenuForChat(userId);
                        return true;
                    }
                });
                FirebaseFirestore.getInstance().collection("counsellors").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        try {
                            final String userName = documentSnapshot.get("name").toString();
                            String thumbImage = documentSnapshot.get("image").toString();
                            viewHolder.setImage(thumbImage, mContext);
                            viewHolder.setName(Handy.getTrimmedName(userName));
                            //----------------------NAVIGATING TO USER PROFILE ACTIVITY------------------------//
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(mContext, ChatActivity.class);
                                    i.putExtra("user_id", userId);
                                    startActivity(i);

                                }
                            });

                            swipeRefreshLayout.setEnabled(true);

                            progressBar.setVisibility(View.GONE);
                        } catch (Exception ei) {
                            Log.d(TAG, "onDataChange: error" + ei.getMessage());
                        }
                    }
                });


            }};


        mUserFriendsList.setAdapter(adapter);
        adapter.startListening();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        super.onStart();
    }

    private void showOptionsMenuForChat(final String from) {
        Log.d(TAG, "showOptionsMenuForChat: "+from);
        AlertDialog.Builder builder=new AlertDialog.Builder(getBaseContext());
        CharSequence [] sequence={"Delete Chat","Delete Conversation"};

        builder.setItems(sequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (i==0){
                    FirebaseDatabase.getInstance().getReference().child("Chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(from).removeValue();

                }else {
                    Snackbar.make(mUserFriendsList,"This will also clear messages",Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("Chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(from).removeValue();
                            CollectionReference documentReference=FirebaseFirestore.getInstance().collection("messages")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(from);
                            documentReference.document().delete();
                        }
                    }).show();

                }
            }
        }).show();




    }


    @Override
    public void onStop() {
        adapter.stopListening();
        super.onStop();
    }


    public static class TrulysChatVH extends RecyclerView.ViewHolder {
        View mView;

        GestureDetector gestureDetector;
        Context context;
        public TrulysChatVH(View itemView,Context context) {
            super(itemView);
            this.context=context;
            mView = itemView;

        }

        public void setImage(String name, Context context) {
            CircleImageView imageView = mView.findViewById(R.id.userpic);

            Glide.with(context).load(name).into(imageView);
        }

        public void setMessage(String message) {
            TextView textView = mView.findViewById(R.id.message);
            textView.setText(message);
        }

        public void setSentDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.lasttime);
            dateTextView.setText(date);


        }

        public void setName(String name) {
            TextView namet = mView.findViewById(R.id.name);
            namet.setText(String.valueOf(name));


        }
    }



}
