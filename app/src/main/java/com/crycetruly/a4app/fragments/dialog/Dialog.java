package com.crycetruly.a4app.fragments.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crycetruly.a4app.R;
import com.crycetruly.a4app.utils.Handy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;


public class Dialog extends DialogFragment {


    private static final String TAG = "CustomHelpDialog";
    public OnInputListener mOnInputListener;
    DatabaseReference groups;


    //widgets
    ProgressBar progressBar;
    private TextInputEditText mInput;
    private Button mActionOk, mActionCancel;

    @Nullable

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.addgroupdialogue, container, false);
        groups = FirebaseDatabase.getInstance().getReference().child("groups");
        mActionCancel = view.findViewById(R.id.action_cancel);

        mActionOk = view.findViewById(R.id.action_ok);
        progressBar = view.findViewById(R.id.submitting);
        mInput = view.findViewById(R.id.input);


        mActionCancel.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Log.d(TAG, "onClick: closing dialog");

                getDialog().dismiss();

            }

        });


        mActionOk.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Log.d(TAG, "onClick: capturing input");


                String input = mInput.getText().toString();

                if (!input.equals("")) {

                        Map<String, Object> addMap = new HashMap<>();
                        addMap.put("name", input);
                        mActionOk.setEnabled(false);
                        mActionCancel.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        addMap.put("created", ServerValue.TIMESTAMP);
                        addMap.put("fitnessNum", Handy.fitnessNumber());
                    groups.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                                .updateChildren(addMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        Toast.makeText(getContext(), "Circle added success", Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {

                                    }

                                    getDialog().dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Error adding circle,thanks try again", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });


                    }



            }

        });


        return view;

    }
    //vars

    @Override

    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            mOnInputListener = (OnInputListener) getActivity();

        } catch (ClassCastException e) {

            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());

        }

    }


    public interface OnInputListener {

        void sendInput(String input);

    }

}

