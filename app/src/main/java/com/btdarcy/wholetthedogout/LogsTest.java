package com.btdarcy.wholetthedogout;

import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class LogsTest {
    public String status;
    public String time;
    public String dog;

    public LogsTest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public LogsTest(String body, String time, String dog) {
        this.status = body;
        this.time = time;
        this.dog = dog;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Status", status);
        result.put("Time", time);
        result.put("Dog", dog);

        return result;
    }
    public String getStatus(){
        return status;
    }
    public String getTime(){
        return time;
    }
    public String getDog(){
        return dog;
    }
}
