package com.btdarcy.wholetthedogout;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Logs {
    public String status;
    public String time;
    public String dog;

    public Logs() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Logs( String body, String time, String dog) {
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
}
