package com.inrotation.andrew.inrotation.model;

import android.util.Log;

import com.google.android.gms.location.places.PlaceReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 11/20/16.
 */
public class DatabaseModifier {

    private FirebaseDatabase database;

    public DatabaseModifier() {
        this.database = FirebaseDatabase.getInstance();
    }


    public void addUserToDatabase(final HostUser createdUser) {
        final DatabaseReference ref = database.getReference("server/saving-data");
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    if (snapshot.getValue() != createdUser.getEmail()) {
                        DatabaseReference userRef = database.getReference("server/saving-data/users");
                        DatabaseReference placeUserRef = userRef.push();
                        placeUserRef.setValue(createdUser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError arg0) {
                System.out.println("The read failed: " + arg0.getCode());
            }
        });
        // DatabaseReference usersRef = ref.child("users");

        /*
        Map<String, HostUser> users = new HashMap<String, HostUser>();
        users.put(String.valueOf(createdUser.hashCode()), createdUser);
        usersRef.setValue(users);*/


    }

    public void addPlaylist(Playlist createdPlaylist) {
        DatabaseReference ref = database.getReference("server/saving-data");
        DatabaseReference playRef = ref.child("playlists");
        //DatabaseReference playlistsRef = ref.push();
        //playlistsRef.setValue(createdPlaylist);

        Map<String, Playlist> playlist = new HashMap<>();
        String[] key = createdPlaylist.owner.split("@");
        playlist.put(key[0], createdPlaylist);

        //playlist.put(String.valueOf(createdPlaylist.hashCode()), createdPlaylist);
        playRef.setValue(playlist);
    }

}