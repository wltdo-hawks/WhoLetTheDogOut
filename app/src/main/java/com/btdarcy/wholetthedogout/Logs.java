package com.btdarcy.wholetthedogout;

import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Logs {
    private String status;
    private String time;
    private String dog;
    private StorageReference pic;

    public Logs() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Logs(String body, String time, String dog, StorageReference pic) {
        this.setStatus(body);
        this.setTime(time);
        this.setDog(dog);
        this.setPic(pic);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Status", getStatus());
        result.put("Time", getTime());
        result.put("Dog", getDog());
        result.put("Pic", getPic());

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
    public StorageReference getPic() {
        return pic;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDog(String dog) {
        this.dog = dog;
    }

    public void setPic(StorageReference pic) {
        this.pic = pic;
    }
}
