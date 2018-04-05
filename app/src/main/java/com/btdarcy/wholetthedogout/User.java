package com.btdarcy.wholetthedogout;

public class User {
    public String Email;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.Email = email;
    }
}
