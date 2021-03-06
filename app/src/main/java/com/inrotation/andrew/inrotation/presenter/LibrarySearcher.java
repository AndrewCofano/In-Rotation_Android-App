package com.inrotation.andrew.inrotation.presenter;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.inrotation.andrew.inrotation.R;
import com.inrotation.andrew.inrotation.model.MyJSONException;
import com.inrotation.andrew.inrotation.model.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

/**
 * Represents the handler of search input for the Spotify Music Library.
 * Created by Andrew Cofano
 */
public class LibrarySearcher {

    private static final String SPOTIFY_SEARCH_URL_STANDARD = "https://api.spotify.com/v1/search?q=";


    protected static ArrayList<Song> obtainSongMatches(String[] queryWords, Context context) {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);

        final ArrayList<Song> returnArray = new ArrayList<>();
        String url = createQuerySearchURL(queryWords);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());

                        try {
                            JSONObject allTracksObject = response.getJSONObject("tracks");
                            JSONArray trackElements = allTracksObject.getJSONArray("items");
                            for (int i = 0; i < trackElements.length(); i++) {
                                JSONObject singleTrack = trackElements.getJSONObject(i);
                                Song trackObject = createSongOf(singleTrack);
                                returnArray.add(trackObject);

                            }
                        } catch (RuntimeException e) {
                            returnArray.clear();
                            throw e;
                            //Log.d("Error", e.toString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //mSongNameView.setText("That didn't work!" + error.toString());
                Log.d("Volley", error.toString());
            }
        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new ArrayMap<>();
                params.put("Accept", "application/json");
                //..add other headers
                return params;
            }
        };

        queue.add(objectRequest);

        return returnArray;
    }

    public static String createQuerySearchURL(String[] queryWords) {
        int index;
        StringBuilder trackSearchURL = new StringBuilder();
        trackSearchURL.append(SPOTIFY_SEARCH_URL_STANDARD);

        index = 0;
        while (index < queryWords.length) {
            if (index != (queryWords.length - 1)) {
                trackSearchURL.append(queryWords[index]);
                trackSearchURL.append("+");
            }
            else {
                trackSearchURL.append(queryWords[index]);
            }
            index++;
        }
        trackSearchURL.append("&type=track&limit=3");

        return trackSearchURL.toString();
    }

    private static Song createSongOf(JSONObject jsonTrack) throws MyJSONException {
        Song newSong;

        try {
            String songName = jsonTrack.getString("name");
            JSONArray artistList = jsonTrack.getJSONArray("artists");
            String artist = artistList.getJSONObject(0).getString("name");
            JSONObject album = jsonTrack.getJSONObject("album");
            String albumName = album.getString("name");
            JSONArray albumCoverList = album.getJSONArray("images");
            ArrayList<String> albumCovers = createAlbumCoverList(albumCoverList);
            int duration =  jsonTrack.getInt("duration_ms");
            String songURI = jsonTrack.getString("uri");
            boolean explicit = jsonTrack.getBoolean("explicit");

            newSong = new Song(songName, artist, albumName, duration, albumCovers, songURI, explicit);
            return newSong;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MyJSONException(e);
        }

    }


    private static ArrayList<String> createAlbumCoverList(JSONArray albumCovers) throws MyJSONException {
        ArrayList<String> albumCoverURLs = new ArrayList<>();

        for (int i = 0; i < albumCovers.length(); i++) {
            try {
                albumCoverURLs.add(albumCovers.getJSONObject(i).getString("url"));
            }
            catch (RuntimeException e) {
                //e.printStackTrace();
                throw e;
            }
            catch (Exception e) {
                throw new MyJSONException(e);
            }
        }

        return albumCoverURLs;
    }

}
