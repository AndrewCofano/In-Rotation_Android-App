package com.inrotation.andrew.inrotation.data_access;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inrotation.andrew.inrotation.model.HostUser;
import com.inrotation.andrew.inrotation.model.Playlist;
import com.inrotation.andrew.inrotation.model.Song;
import com.inrotation.andrew.inrotation.model.SpotifyAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the access to the Firebase database, for writing data.
 * Created by Andrew Cofano on 11/20/16.
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
                if (snapshot.getValue() == null && snapshot.getValue() != createdUser.getEmail()) {
                    DatabaseReference userRef = database.getReference("server/saving-data/users");
                    DatabaseReference placeUserRef = userRef.push();
                    placeUserRef.setValue(createdUser);
                }
            }
            @Override
            public void onCancelled(DatabaseError arg0) {
                System.out.println("The read failed: " + arg0.getCode());
            }
        });

    }

    public void addPlaylist(Playlist createdPlaylist, Song firstSong) {
        DatabaseReference ref = database.getReference("server/saving-data/playlists");

        Map<String, Playlist> playlist = new HashMap<>();
        String[] key = createdPlaylist.owner.split("@");
        playlist.put(key[0], createdPlaylist);

        ref.setValue(playlist);
        addFirstSong(key[0], firstSong);
    }

    private void addFirstSong(String playlistCode, Song selectedSong) {
        DatabaseReference ref = database.getReference("server/saving-data/playlists/"+playlistCode+"/");
        DatabaseReference listRef = ref.child("songArrayList");

        DatabaseReference newSongRef = listRef.push();
        String songID = newSongRef.getKey();

        DatabaseReference currentSongRef = ref.child("currentSongIndex");
        currentSongRef.setValue(songID);
        newSongRef.setValue(selectedSong);
        SpotifyAccess spotifyAccess = SpotifyAccess.getInstance();
        spotifyAccess.setSongList(new ArrayList<Song>());
        spotifyAccess.getSongList().add(selectedSong);
    }

    public void addSongToPlaylist(String playlistCode, Song selectedSong) {
        DatabaseReference ref = database.getReference("server/saving-data/playlists/"+playlistCode+"/");
        DatabaseReference listRef = ref.child("songArrayList");

        DatabaseReference newSongRef = listRef.push();
        newSongRef.setValue(selectedSong);

    }
}
