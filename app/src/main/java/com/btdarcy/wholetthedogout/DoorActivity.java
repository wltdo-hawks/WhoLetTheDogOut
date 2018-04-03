package com.btdarcy.wholetthedogout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class DoorActivity extends AppCompatActivity {

    private Switch doorSwitch;
    private TextView doorStatus;
    private ListView doorLog;
    private Button btnAccount;
    private ImageView mImageView;
    private boolean checked;
    private String[] status = {"Opened at ","Closed at ","Opened at ","Closed at "};
    private String[] time = {"12:30","12:32","2:46","2:50"};
    private String[] dog = {"By Lucy","By Lucy", "By Bella", "By Bella"};
    Integer[] imgid = {R.mipmap.lucydoortest1, R.mipmap.lucydoortest2, R.mipmap.lucydoortest3, R.mipmap.lucydoortest4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        final Switch doorSwitch = (Switch) findViewById(R.id.door_switch);
        final TextView doorStatus = (TextView) findViewById(R.id.door_status);
        ListView doorlog = (ListView) findViewById(R.id.list);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://wltdo-9af27.appspot.com").child("lucydoortest1.jpg");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("message").child("part 1");
        final DatabaseReference myRef2 = database.getReference("message").child("part 2");
        final DatabaseReference doorStatusData = database.getReference("Flags").child("Door Status");
        final DatabaseReference logData = database.getReference("Logs");
        final DatabaseReference statusLog = database.getReference("Logs").child("Status");
        final DatabaseReference dogID = database.getReference("Logs").child("Dog ID");

        CustomListview customListview = new CustomListview(this, status, dog, imgid);
        doorlog.setAdapter(customListview);

        btnAccount = findViewById(R.id.account);


        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoorActivity.this, AccountInfoActivity.class));
            }
        });

        dogID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //updateView(1,value);
                 dog = new String[]{value, value, value, value};
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        doorStatusData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                doorSwitch.setChecked(isChecked(value));
                doorStatus.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        doorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true)
                {
                    doorStatus.setText("Open");
                    doorStatus.setTextColor(Color.GREEN);
                    doorStatusData.setValue("Open");
                }
                else if(b==false)
                {
                    doorStatus.setText("Closed");
                    doorStatus.setTextColor(Color.RED);
                    doorStatusData.setValue("Closed ");
                }

            }
        });
    }

    public boolean isChecked(String state){
        boolean b = false;
        if(state.equals("Open"))
        {
            b = true;
        }
        else if(state.equals("Closed"))
        {
            b = false;
        }
        return b;
    }

    /*private void updateView(int index, String change){
        View v = doorLog.getChildAt(index - doorLog.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.status);
        someText.setText(change);
    }*/
}
