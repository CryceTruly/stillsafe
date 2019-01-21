package com.crycetruly.a4app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.a4app.AuthActivity;
import com.crycetruly.a4app.CircleDetailActivity;
import com.crycetruly.a4app.R;
import com.crycetruly.a4app.fragments.dialog.Dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    FloatingActionButton btn;
    DatabaseReference reference;
    FirebaseAuth auth;
    RelativeLayout main;
    TextView more, name;
    private static final String TAG = "GroupsFragment";
    LinearLayout test;

    TextView count;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            sendToAuth();
        }else{
            Log.d(TAG, "onCreate: "+user.getPhoneNumber());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        auth = FirebaseAuth.getInstance();
        test = v.findViewById(R.id.test);
        main = v.findViewById(R.id.main);
 count=v.findViewById(R.id.countting);

        CardView cardView=v.findViewById(R.id.card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                startActivity(new Intent(getContext(),CircleDetailActivity.class));
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                startActivity(new Intent(getContext(),CircleDetailActivity.class));
            }
        });

        name = v.findViewById(R.id.name);



        return v;


    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        try {
            reference = FirebaseDatabase.getInstance().getReference().child("groups")
                    .child(auth.getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() ==0) {
                        test.setVisibility(View.GONE);
                        Snackbar.make(main, "You have not created a circle yet ",
                                Snackbar.LENGTH_INDEFINITE).setAction("Create", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Dialog dialog = new Dialog();
                                dialog.show(getFragmentManager(), "Dialog");
                         dialog.setCancelable(false);
                                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                            }
                        }).show();
                    } else {

                        Log.d(TAG, "onDataChange: yes group");

                        for(DataSnapshot single:dataSnapshot.getChildren()){
                            final String key = single.getKey();
try {
    String nameE = single.child("name").getValue().toString();

    name.setText(nameE);
}catch (NullPointerException e){


}
                                   FirebaseDatabase.getInstance().getReference().child("Friends")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()){
                                                Log.d(TAG, "onDataChange: not exists");
                                                count.setText("No People yet");
                                            }else {
                                                Log.d(TAG, "onDataChange: else");
                                                long countt = dataSnapshot.getChildrenCount();
                                                if (countt == 1) {
                                                    count.setText(String.valueOf(countt).concat(" person"));

                                                }
                                               else {
                                                    count.setText(String.valueOf(countt).concat(" people"));
                                                }

                                            }
                                            }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            test.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: "+key);
                                    Intent i = new Intent(getContext(), CircleDetailActivity.class);
                                    i.putExtra("key", key);
                                    startActivity(i);
                                }
                            });
                        }




                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (NullPointerException e) {
            sendToAuth();
        }

        super.onStart();
    }

    private void sendToAuth() {
        Log.d(TAG, "sendToAuth: ");
        Intent i = new Intent(getContext(), AuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
}