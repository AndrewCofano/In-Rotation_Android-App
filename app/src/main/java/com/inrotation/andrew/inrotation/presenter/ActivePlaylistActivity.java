package com.inrotation.andrew.inrotation.presenter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.inrotation.andrew.inrotation.model.DatabaseModifier;
import com.inrotation.andrew.inrotation.model.Playlist;
import com.inrotation.andrew.inrotation.model.PlaylistManager;
import com.inrotation.andrew.inrotation.model.SearchLibrary;
import com.inrotation.andrew.inrotation.model.Song;
import com.inrotation.andrew.inrotation.model.SpotifyAccess;
import com.inrotation.andrew.inrotation.R;
import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;

public class ActivePlaylistActivity extends AppCompatActivity {

    protected Song firstSong;
    protected ArrayList<Song> songList;
    //protected String playlistName;
    protected ListView mListView;
    protected Player mPlayer;
    protected SearchListAdapter adapter;
    protected PlaylistManager playlistManager;

    protected String playlistKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songList = new ArrayList<>();
        Bundle b = this.getIntent().getExtras();
        /*
        if (b != null) {
            firstSong = (Song) b.get("firstSong");
            songList.add(firstSong);
            playlistName = b.getString("playlistName");
        }
        */

        setContentView(R.layout.activity_active_playlist);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SpotifyAccess spotifyAccess = SpotifyAccess.getInstance();
        playlistKey = spotifyAccess.getSpotifyUser().getPlaylistToken();
        setPlaylistName(playlistKey, myToolbar);

        //getSupportActionBar().setTitle(builder.toString());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/playlists/"+playlistKey+"/songArrayList");
        // Attach a listener to read the data at our posts reference
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Song post = dataSnapshot.getValue(Song.class);
                songList.add(post);
                populateSongListView();
                adapter.setmDataSource(songList);
                mListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

        });

        final FloatingActionButton joinPlaylistButton = (FloatingActionButton) findViewById(R.id.AddSongActionButton);
        joinPlaylistButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addSongIntent = new Intent(v.getContext(), AddSongActivity.class);
                startActivity(addSongIntent);
            }
        });


        playlistManager = new PlaylistManager();
        final ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playlistManager.onPlayButtonClicked(playButton);
            }
        });

        final ImageButton rewindButton = (ImageButton) findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playlistManager.onPreviousButtonClicked(playButton);
            }
        });

        final ImageButton fastForwardButton = (ImageButton) findViewById(R.id.fastforwardButton);
        fastForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playlistManager.onNextButtonClicked(playButton);
            }
        });

        /*SpotifyAccess spotifyAccessInstance = SpotifyAccess.getInstance();
        mPlayer = spotifyAccessInstance.getSpotifyPlayer();
        mPlayer.playUri(null, firstSong.songURI, 0, 0);*/
    }

    protected void populateSongListView() {

        mListView = (ListView) findViewById(R.id.songListView);

        adapter = new SearchListAdapter(this, songList);
        mListView.setAdapter(adapter);
    }

    public void setPlaylistName(String code, final Toolbar mytoolbar) {
        //final StringBuilder playlistName = new StringBuilder();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/playlists/" + code);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Playlist post = dataSnapshot.getValue(Playlist.class);
                getSupportActionBar().setTitle(post.playlistName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        populateSongListView();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/playlists/");

        ref.child(playlistKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Playlist post = snapshot.getValue(Playlist.class);
                SpotifyAccess spotifyAccessInstance = SpotifyAccess.getInstance();
                mPlayer = spotifyAccessInstance.getSpotifyPlayer();
                if (!mPlayer.getPlaybackState().isPlaying) {
                    spotifyAccessInstance.setCurrentSong(post.getSongArrayList().get(0));
                    mPlayer.playUri(null, post.getSongArrayList().get(0).songURI, 0, 0);
                    /**
                     * Add code to change play button to pause button!!!!!!!!!!!!!
                     */
                }


            }
            @Override
            public void onCancelled(DatabaseError arg0) {
                System.out.println("The read failed: " + arg0.getCode());
            }
        });
        ViewRefresher.refreshPlayerBar(this, this);



    }


}