package com.crycetruly.a4app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.a4app.utils.GetCurTime;
import com.crycetruly.a4app.utils.Handy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.INPUT_METHOD_SERVICE;

import com.crycetruly.a4app.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMemberFragment extends Fragment {
    Query firebaseSearchQuery;
    private ImageView seachimage;
    private EditText searchedittext;
    private RecyclerView userList;
    private Context mContext;
    private DatabaseReference mDatabase;
    private ProgressBar progresss;
    private TextView textView;
    private DatabaseReference friendRef, users;
    FirebaseRecyclerAdapter adapter;
    TextView name;
    ImageView add;
    private static final String TAG = "AddMemberFragment";
    RelativeLayout rl;

    public AddMemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = getLayoutInflater().inflate(R.layout.fragment_add_member, container, false);
        progresss = v.findViewById(R.id.progresss_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        rl = v.findViewById(R.id.rl);
        mContext = getContext();
        seachimage = v.findViewById(R.id.search);
        searchedittext = v.findViewById(R.id.searchUserField);
        name = v.findViewById(R.id.name);
        textView = v.findViewById(R.id.textView);
        searchedittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    firebaseUserSearch(Handy.capitalize(searchedittext.getText().toString().trim()), v);
                }
                return false;
            }
        });

        seachimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = Handy.capitalize(searchedittext.getText().toString().trim());
                if (!TextUtils.isEmpty(searchText)) {
                    progresss.setVisibility(View.VISIBLE);
                    firebaseUserSearch(Handy.capitalize(searchText), v);
                    hideSoftKeyboard();
                } else {
                    Toast.makeText(mContext, "Type a phone of user to search", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return v;
    }

    private void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void firebaseUserSearch(final String searchText, View view) {
        Log.d(TAG, "firebaseUserSearch: ");
        hideSoftKeyboard();
        name = view.findViewById(R.id.name);
        textView.setVisibility(View.GONE);
        firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone")
                .equalTo(searchText);

        firebaseSearchQuery.keepSynced(true);
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: not exists");
                    Toast.makeText(mContext, "No matching users for the search", Toast.LENGTH_SHORT).show();
                    progresss.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onDataChange: again not");
                } else {
                    progresss.setVisibility(View.GONE);
                    Log.d(TAG, "onDataChange: ");
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Toast.makeText(mContext, "Found User", Toast.LENGTH_SHORT).show();

                        final String user_id = snapshot.getKey();
                        if (user_id != FirebaseAuth.getInstance().getUid()) {


                            Snackbar.make(rl, snapshot.child("phone").getValue().toString(), Snackbar.LENGTH_INDEFINITE).setAction("Add", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final DatabaseReference otherUserRecievedRequest = FirebaseDatabase.getInstance().getReference().child("RecievedRequests").child(user_id);


                                    // --------------- NOT FRIENDS STATE ------------


                                    final FirebaseUser mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
                                    Toast.makeText(getContext(), "sending invite", Toast.LENGTH_SHORT).show();
                                    DatabaseReference newNotificationref = FirebaseDatabase.getInstance().getReference().child("Notifications").child(user_id).push();
                                    String newNotificationId = newNotificationref.getKey();

                                    HashMap<String, String> notificationData = new HashMap<>();
                                    notificationData.put("from", FirebaseAuth.getInstance().getUid());
                                    notificationData.put("type", "request");
                                    notificationData.put("who", user_id);

                                    Map<String, Object> requestMap = new HashMap<>();
                                    requestMap.put("Requests/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                                    requestMap.put("Requests/" + mCurrent_user.getUid() + "/" + user_id + "/user_id", user_id);
                                    requestMap.put("Requests/" + mCurrent_user.getUid() + "/" + user_id + "/time", GetCurTime.getCurTime());
                                    requestMap.put("Requests/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                                    requestMap.put("Requests/" + user_id + "/" + mCurrent_user.getUid() + "/sent", GetCurTime.getCurTime());
                                    requestMap.put("Notifications/" + user_id + "/" + newNotificationId, notificationData);
                                    Map<String, Serializable> map = new HashMap<String, Serializable>();
                                    map.put("time", GetCurTime.getCurTime());
                                    map.put("fitnessNum", Handy.fitnessNumber());
                                    map.put("sent", GetCurTime.getCurTime());
                                    otherUserRecievedRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);

                                    FirebaseDatabase.getInstance().getReference().updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                            if (databaseError != null) {


                                            } else {

                                                Toast.makeText(mContext, "Circle Join invite Sent", Toast.LENGTH_SHORT).show();
                                                Snackbar.make(rl, "Invite sent ", Snackbar.LENGTH_INDEFINITE).setAction("Cancel", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Map<String, Object> friendsMap = new HashMap<>();
                                                        friendsMap.put("Requests/" + mCurrent_user.getUid() + "/" + user_id, null);
                                                        friendsMap.put("Requests/" + user_id + "/" + mCurrent_user.getUid(), null);

                                                        otherUserRecievedRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);
                                                        FirebaseDatabase.getInstance().getReference().updateChildren(friendsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getContext(), "Invite Cancelled", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).show();

                                            }


                                        }
                                    });


                                }
                            }).show();

                        }else{
                            Toast.makeText(mContext, "No need to find self", Toast.LENGTH_SHORT).show();

                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        try {
            adapter.startListening();
        } catch (NullPointerException E) {

        }
        super.onStart();
    }

    @Override
    public void onStop() {
        try {
            adapter.stopListening();
        } catch (NullPointerException e) {

        }
        super.onStop();
    }

    public static class TrulysPeopleVH extends RecyclerView.ViewHolder {
        View mView;
        TextView us_name, us_status;
        CircleImageView us_image;
        ImageView add;

        public TrulysPeopleVH(View itemView) {
            super(itemView);
            mView = itemView;
            us_name = mView.findViewById(R.id.name);
            us_image = mView.findViewById(R.id.attendee_pic);

            add = mView.findViewById(R.id.add);

        }

        public void setDetails(String name) {
            TextView us_name = mView.findViewById(R.id.name);
            us_name.setText(Handy.getTrimmedName(name));
        }


    }

}
