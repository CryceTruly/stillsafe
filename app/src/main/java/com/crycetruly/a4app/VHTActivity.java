package com.crycetruly.a4app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crycetruly.a4app.models.VHT;
import com.crycetruly.a4app.utils.Preferences;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class VHTActivity extends AppCompatActivity {
    Toolbar toolbar;
private FirestoreRecyclerAdapter adapter;
ProgressBar progressBar;
private String phone;
TextView test;
    private static final String TAG = "VHTActivity";
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vht);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        test=findViewById(R.id.test);
        getSupportActionBar().setTitle("VHT Members in my Village");
        String b=getIntent().getStringExtra("document");
        Log.d(TAG, "onCreate: from the village :"+b);

         recyclerView=findViewById(R.id.list);
        progressBar=findViewById(R.id.progress2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        com.google.firebase.firestore.Query query;
      try {
           query = FirebaseFirestore.getInstance()
                  .collection("vhtmembers").document(
                          Preferences.getLocality(this)
                  ).collection(b);
      }catch (IllegalArgumentException e){
          query = FirebaseFirestore.getInstance()
                  .collection("vhtmembers").document("Mbarara")
                  .collection(b);//.whereEqualTo("locality",);
      }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "onEvent: "+documentSnapshots.toString());
                if (documentSnapshots.getDocuments().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    test.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "onEvent: "+documentSnapshots.getDocuments());
            }
        });
        FirestoreRecyclerOptions<VHT> options=new FirestoreRecyclerOptions.Builder<VHT>().setQuery(
                query,VHT.class
        ).build();

         adapter=new FirestoreRecyclerAdapter<VHT,VHTHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final VHTHolder holder, int position, @NonNull final VHT model) {
                holder.setgender(model.getGender());
                holder.setName(model.getName());
                holder.setProfile(model.getProfile());
                try{
                    FirebaseStorage.getInstance().getReference().child(model.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                          String  photourl= String.valueOf(uri);
                            RequestOptions requestOptions=new RequestOptions().placeholder(R.drawable.consellor);
                            try {
                                Glide.with(getBaseContext()).load(photourl).apply(requestOptions).into(holder.photo);
                            }catch (NullPointerException e){

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }catch (Exception e){

                }
                progressBar.setVisibility(View.GONE);
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(holder.mView,"Text "+model.getName(),Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: ");
                                smsNumber(model.getPhone());
                            }
                        }).show();
                    }
                });
                holder.c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(holder.mView,"Call "+model.getName(),Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               call(model.getPhone());
                            }
                        }).show();
                    }
                });
recyclerView.setAdapter(adapter);
            }

            @Override
            public VHTHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_vht,parent,false);
                return new VHTHolder(v);
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void smsNumber(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
            smsIntent.putExtra("sms_body", "Hi i need medical attention,can u help me");
            smsIntent.putExtra("sms_title", "Keep Safe Help");
            if (smsIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(smsIntent);
            } else {
                Toast.makeText(this, "No apps to handle data", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void call(String phone) {
        Log.d(TAG, "call: ");
            Log.d(TAG, "onOptionsItemSelected: call");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));

        try {
            if (!phone.equals("")) {
                if (intent.resolveActivity(getPackageManager()) != null) {

                    String dial = "tel:" + phone;
                    try {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    }catch (ActivityNotFoundException e){
                        Toast.makeText(this, "No apps", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Unable to handle data", Toast.LENGTH_SHORT).show();
                }


                Log.d(TAG, "onOptionsItemSelected: calling");


            } else {
                Toast.makeText(this, "Unavailable", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }

    private static class VHTHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView photo;
        RelativeLayout relativeLayout;
        ImageView c;
        public VHTHolder(View itemView) {
            super(itemView);
            mView=itemView;
            photo=mView.findViewById(R.id.photo);
            c=mView.findViewById(R.id.c);
            relativeLayout=mView.findViewById(R.id.rel);

        }
        public void setName(String name){
            TextView textView=mView.findViewById(R.id.name);
            textView.setText(name);
        }

        public void setProfile(String profile){
            TextView textView=mView.findViewById(R.id.desc);
            textView.setText(profile);
        }
        public void setgender(String gender){
            TextView textView=mView.findViewById(R.id.gender);
            textView.setText(gender);
        }


    }
}
