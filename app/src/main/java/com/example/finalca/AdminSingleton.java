package com.example.finalca;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminSingleton {

    private static AdminSingleton instance;
    private DatabaseReference mDatabase;

    private AdminSingleton() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized AdminSingleton getInstance() {
        if (instance == null) {
            instance = new AdminSingleton();
        }
        return instance;
    }

    public DatabaseReference getReference() {
        return mDatabase;
    }
}
