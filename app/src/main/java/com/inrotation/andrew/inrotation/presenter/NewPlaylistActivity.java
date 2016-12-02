package com.inrotation.andrew.inrotation.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.inrotation.andrew.inrotation.model.DatabaseModifier;
import com.inrotation.andrew.inrotation.model.NewPlaylistCreator;
import com.inrotation.andrew.inrotation.model.Playlist;
import com.inrotation.andrew.inrotation.model.SearchLibrary;
import com.inrotation.andrew.inrotation.model.Song;
import com.inrotation.andrew.inrotation.R;
import com.inrotation.andrew.inrotation.model.SpotifyAccess;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/18/16.
 */

public class NewPlaylistActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ListView mListView;
    private Playlist createdPlaylist;
    private NewPlaylistCreator playlistCreator;
    private DatabaseModifier dbModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("New Playlist");

        playlistCreator = new NewPlaylistCreator();
        playlistCreator.setPlaylistName(getSupportActionBar().getTitle().toString());

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExpectPlaylistConfigInput();
        ExpectFirstSongPick();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void ExpectFirstSongPick() {
        final EditText editTextName = (EditText) findViewById(R.id.FirstSongNameSearch);
        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String songSearches = editTextName.getText().toString();
                    presentSongMatches(songSearches);
                    handled = true;
                }

                return handled;
            }
        });
    }

    protected void presentSongMatches(String searchInput) {
        mListView = (ListView) findViewById(R.id.songSearchListView);

        final ArrayList<Song> songMatches = SearchLibrary.obtainSongMatches(searchInput.split(" "), this);
        SearchListAdapter adapter = new SearchListAdapter(this, songMatches);
        Log.d("SongMatches", songMatches.toString());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Song selectedSong = songMatches.get(position);
                startActivePlaylist(selectedSong);
            }
        });
    }


    protected void startActivePlaylist(Song selectedSong) {

        playlistCreator.setFirstPlaylistSong(selectedSong);
        createdPlaylist = playlistCreator.createNewPlaylist();
        SpotifyAccess accessInstance = SpotifyAccess.getInstance();
        accessInstance.getSpotifyUser().setActivePlaylist(createdPlaylist);

        dbModifier = new DatabaseModifier();
        dbModifier.addPlaylist(createdPlaylist, selectedSong);


        Intent i = new Intent(this, ActivePlaylistActivity.class);
        i.putExtra("playlistCode", accessInstance.getSpotifyUser().getPlaylistToken());
        startActivity(i);


        //Sending first song to new active playlist
    }

    protected void ExpectPlaylistConfigInput() {
        final EditText editTextName = (EditText) findViewById(R.id.searchPlaylistCode);
        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setPlaylistName(editTextName.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextName.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    handled = true;
                }
                return handled;
            }
        });
        Button doneButton = (Button) findViewById(R.id.PlaylistNameDoneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Done buton", "clicked");
                setPlaylistName(editTextName.getText().toString());
            }
        });


    }


    protected void setPlaylistName(String newName) {

        if (!newName.isEmpty()) {
            getSupportActionBar().setTitle(newName);
            playlistCreator.setPlaylistName(newName);

        }


    }



}
