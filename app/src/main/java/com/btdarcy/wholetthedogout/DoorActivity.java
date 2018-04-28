package com.btdarcy.wholetthedogout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class DoorActivity extends AppCompatActivity {

    private Switch doorSwitch;
    private Button openbtn;
    private TextView doorStatus;
    private Button btnAccount;
    private ImageView mImageView;
    private FirebaseAuth mAuth;
    private RecyclerView logView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference userData, openRequest, doorState, flags,logs, statusLog,dogID, test1;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef,storageRef2, openPic, closePic;
    private FirebaseUser user;
    private List<Logs> listItems;
    private boolean idle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        mAuth = FirebaseAuth.getInstance();
        //doorSwitch = findViewById(R.id.door_switch);
        doorStatus = findViewById(R.id.door_status);
        btnAccount = findViewById(R.id.account);
        mImageView = findViewById(R.id.current_pic);
        openbtn = findViewById(R.id.open_button);
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com/pictures/lucydoortest1.jpg");
        openPic = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com/pictures/opendoor.png");
        closePic = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com/pictures/closedoor.png");
        database = FirebaseDatabase.getInstance();
        userData = database.getReference("Users").child(user.getUid());
        flags = userData.child("Flags");
        openRequest = flags.child("OpenRequest");
        doorState = flags.child("DoorState");
        logs = userData.child("Logs");
        statusLog = logs.child("status");
        dogID = logs.child("dog");
        test1 = database.getReference("test1data").child("data");

        logView = findViewById(R.id.log_view);
        logView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        logView.setLayoutManager(mLayoutManager);
        listItems = new ArrayList<>();
        adapter = new MyAdapter(listItems, this);
        logView.setAdapter(adapter);

        Glide.with(this )
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(mImageView);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoorActivity.this, AccountInfoActivity.class));
            }
        });

        openRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value == null)
                {
                    openRequest.setValue("Close");
                }
                if(value.equals("End"))
                {
                    //doorSwitch.setChecked(false);
                    openbtn.setText("Open");
                }else if(value.equals("Open"))
                {
                    openbtn.setText("waiting");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRequest.setValue("Open");
                openbtn.setText("Waiting");
                /*doorSwitch.setChecked(true);
                idle = false;*/
            }
        });

        doorState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                if(value == null)
                {
                    doorState.setValue("Close");
                }

                    if(value != null)
                    {
                        doorStatus.setText(value);
                        if(value.equals("Open"))
                        {
                            doorStatus.setText(R.string.open);
                            doorStatus.setTextColor(Color.GREEN);
                        }
                        else if(value.equals("Close"))
                        {
                            doorStatus.setText(R.string.closed);
                            doorStatus.setTextColor(Color.RED);
                        }

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*doorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                *//*if(b & !idle)
                {
                    openRequest.setValue("Open");
                }
                else if(!b & !idle)
                {
                    openRequest.setValue("Close");
                }*//*

            }
        });*/
/*        doorSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doorSwitch.setChecked(doorSwitch.isChecked());
            }
        });*/

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Logs data = dataSnapshot.getValue(Logs.class);

                updateLogs(data);

                listItems.add(0,data);
                while(listItems.size()>4)
                {
                    listItems.remove(listItems.size()-1);
                }
                logView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //test1.addChildEventListener(childEventListener);
        logs.addChildEventListener(childEventListener);
    }

    /*public boolean isChecked(String state){
        boolean b = false;
        if(state.equals("Open"))
        {
            b = true;
        }
        else if(state.equals("Close"))
        {
            b = false;
        }
        return b;
    }*/



    private void updateLogs(Logs data)
    {
        String state = data.getStatus();
        if(state != null) {
            switch (state) {
                case "Opened by ":
                    data.setPic(closePic);
                    //data.setPic(openPic);
                    break;

                case "Closed by ":
                    data.setPic(openPic);
                    //data.setPic(closePic);
                    break;

                default:

                    break;
            }
        }

        String dog = data.getDog();
        if(dog != null) {
            switch (dog) {
                case "1":
                    data.setDog("Lucy");
                    break;

                case "2":
                    data.setDog("Not Lucy");
                    break;

                default:

                    break;
            }
        }

        formatTime(data);

    }

    private void formatTime(Logs data){
        String time = data.getTime();
        String[] ftime = time.split("T");
        String[] temp = ftime[1].split(":");
        String[] sec = temp[2].split("\\.");
        String daytime;
        int h = Integer.parseInt(temp[0]);
        h = h - 4;
        if(h < 0){
            h = h + 24;
        }
        if(h > 12) {
            h = h - 12;
            daytime = "PM";
        }else if (h == 0){
            h = 12;
            daytime = "AM";
        }else{
            daytime = "AM";
        }
        String hour = String.valueOf(h);
        String ttime = hour + ":" +temp[1] + ":" +sec[0] + " " + daytime;
        String[] tdate = ftime[0].split("-");
        String date = tdate[1] + "/" + tdate[2] + "/" + tdate[0];
        data.setTime(ttime  + " " + date);
    }
}
