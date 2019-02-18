package com.crycetruly.a4app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crycetruly.a4app.adapters.VillagesAdapter;
import com.crycetruly.a4app.fragments.HomeFragment;
import com.crycetruly.a4app.models.Healthunit;
import com.crycetruly.a4app.models.Village;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerchActivity extends AppCompatActivity {
    private static final String TAG = "SerchActivity";
    private RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    private Toolbar toolbar;
    ProgressBar test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        test=findViewById(R.id.test);
        Log.d(TAG, "onCreate: started");
        toolbar = findViewById(R.id.searchToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose village");


        //SET UP PREFERENCES TO SORT THE SEARCHES------------------------------//
        recyclerView = findViewById(R.id.evv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
     Query query = FirebaseDatabase.getInstance()
                .getReference().child("villages").child("Mbarara");

        //todo afix for location on phones probaby build a serve with no failure issues.whereEqualTo("locality", Preferences.getLocality(getContext()));


        FirebaseRecyclerOptions<Village> options = new FirebaseRecyclerOptions.Builder<Village>()
                .setQuery(query, Village.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Village, Holder>(options) {
            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.single_ev,parent,false);
                return new Holder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull final Village model) {
                holder.setsetVillage(model.getName());
                test.setVisibility(View.GONE);
               final String key=getRef(position).getKey();

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(getBaseContext(),VHTActivity.class);
                        i.putExtra("document",model.getName());
                        startActivity(i);
                    }
                });

            }


        };
        recyclerView.setAdapter(adapter);
        };

    //---------------------------SETUP NAVIGATION VIEW------------------------------/

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

    @Override
    public void onBackPressed() {
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu)

    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private static class Holder extends RecyclerView.ViewHolder {
       View v;
        private Holder(View itemView) {
            super(itemView);
            v=itemView;

        }
        public void setsetVillage(String name){
            TextView view=v.findViewById(R.id.ev_title);
            view.setText(name);
        }
    }
}
