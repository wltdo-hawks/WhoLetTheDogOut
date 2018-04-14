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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class DoorActivity extends AppCompatActivity {

    private Switch doorSwitch;
    private TextView doorStatus;
    private Button btnAccount;
    private ImageView mImageView;
    private FirebaseAuth mAuth;
    private RecyclerView logView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference userData, doorStatusData, doorState, flags,logs, statusLog,dogID;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef,storageRef2;
    private FirebaseUser user;
    private List<Logs> listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        mAuth = FirebaseAuth.getInstance();
        doorSwitch = findViewById(R.id.door_switch);
        doorStatus = findViewById(R.id.door_status);
        btnAccount = findViewById(R.id.account);
        mImageView = findViewById(R.id.current_pic);
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com/pictures/lucydoortest1.jpg");
        storageRef2 = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com/pictures/pic.jpg");
        database = FirebaseDatabase.getInstance();
        userData = database.getReference("Users").child(user.getUid());
        flags = userData.child("Flags");
        doorStatusData = flags.child("Door Status");
        doorState = flags.child("Door State");
        logs = userData.child("Logs");
        statusLog = logs.child("status");
        dogID = logs.child("dog");

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

        doorStatusData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value == null)
                {
                    doorStatusData.setValue("Close");
                    doorState.setValue("Closed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        doorState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        doorState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                    if(value != null)
                    {
                        doorSwitch.setChecked(isChecked(value));
                        doorStatus.setText(value);
                        if(value.equals("Open"))
                        {
                            doorStatus.setText(R.string.open);
                            doorStatus.setTextColor(Color.GREEN);
                            doorStatusData.setValue("Open");
                        }
                        else if(value.equals("Close"))
                        {
                            doorStatus.setText(R.string.closed);
                            doorStatus.setTextColor(Color.RED);
                            doorStatusData.setValue("Close");
                        }
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        doorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    doorStatusData.setValue("Open");
                }
                else if(!b)
                {
                    doorStatusData.setValue("Close");
                }

            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Logs data = dataSnapshot.getValue(Logs.class);

                String state = data.getStatus();
                if(state != null) {
                    switch (state) {
                        case "Opened at":
                            data.setPic(storageRef);
                            break;

                        case "Closed at":
                            data.setPic(storageRef2);
                            break;

                        default:

                            break;
                    }
                }
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
        logs.addChildEventListener(childEventListener);
    }

    public boolean isChecked(String state){
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
    }

    private void writeNewMessage(String body, String dog ,StorageReference mPic) {
        String time = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(Calendar.getInstance().getTime());
        Logs log = new Logs(body, time , dog ,mPic);
        listItems.add(log);
        adapter = new MyAdapter(listItems, getApplicationContext());
        logView.setAdapter(adapter);

        LogsTest log2 = new LogsTest(log.getStatus(), log.getTime(), log.getDog());
        Map<String, Object> childUpdates = new HashMap<>();
        String key = userData.child("Logs").push().getKey();

        childUpdates.put("/Logs/" + key, log2);

        userData.updateChildren(childUpdates);
    }
}
