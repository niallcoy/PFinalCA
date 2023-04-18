package com.example.finalca;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSingleton {

    private static UserSingleton instance;
    private DatabaseReference mDatabase;
    private User user;


    private UserSingleton() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public DatabaseReference getReference() {
        return mDatabase;
    }

    public void setUser(User user) {
        mDatabase.child("users").child(user.getEmail().replace(".", ",")).setValue(user);
    }


}
