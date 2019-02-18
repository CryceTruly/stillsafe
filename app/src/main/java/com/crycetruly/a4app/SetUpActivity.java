package com.crycetruly.a4app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SetUpActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
private DatabaseReference users;
private TextInputEditText nae,pass;
Button button;
RelativeLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        nae=findViewById(R.id.name);
        pass=findViewById(R.id.pass);
        button=findViewById(R.id.toggleButton);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Finish Set Up");
        main=findViewById(R.id.main);

        users= FirebaseDatabase.getInstance().getReference().child("Users");
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){
                    nae.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nae.getVisibility()==View.GONE){
                    String password=pass.getText().toString();
                    button.setEnabled(false);
                    button.setText("Signing In");
                    if(password.length()>=6){
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("password").equalTo(password).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Intent i=new Intent(SetUpActivity.this,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }else{
                                    button.setEnabled(true);
                                    button.setText("Finish Sign In");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    String name=nae.getText().toString();
                    String password=pass.getText().toString();
                    button.setEnabled(false);
                    button.setText("Finishing set up");
                    if(!TextUtils.isEmpty(name)&&(password.length()>5)){
                        Map map=new HashMap();
                        map.put("name",name);
                        map.put("password",password);
                        map.put("phone",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        map.put("device_token", FirebaseInstanceId.getInstance().getToken());


                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Intent i=new Intent(SetUpActivity.this,MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });


                    }else{
                        button.setEnabled(true);
                        button.setText("Finish");
                        Snackbar.make(main,"Name is required "+"\n"+ " Pasword atleat 6 Characters",Snackbar.LENGTH_SHORT).show();

                    }


                }
            }
        });



    }
}
