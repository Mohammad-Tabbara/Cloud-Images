package com.example.mohammad.cloudimages.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.mohammad.cloudimages.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectPhotosFragment extends Fragment {

    private String api_key;

    public InspectPhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            api_key = getApiKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View rootView = inflater.inflate(R.layout.fragment_inspect_photos, container, false);
        EditText searchText = rootView.findViewById(R.id.edit_search);
        searchText.setText("Love");
        Flickr flickr = new Flickr(api_key);
        final PhotosInterface photoInterface = flickr.getPhotosInterface();

        //TODO:ADD SEARCH By Tag
        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setTags(searchText.getText().toString().split(" "));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                PhotoList photos;
                try {
                    photos = photoInterface.search(searchParameters, 10, 0);
                    Photo photo = photos.get(0);
                    String photoURL = photo.getUrl();
                    Log.d("PHOTO_URL", photoURL);
                } catch (IOException | FlickrException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public String getApiKey() throws IOException, JSONException {
        String apiKey;
        InputStream inputStream = getActivity().getAssets().open("api_keys.json");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        String json = new String(buffer, "UTF-8");
        Gson gson = new Gson();
        JSONObject jObject = new JSONObject(json);
        apiKey = jObject.getString("flickr_api_key");
        return apiKey;
    }
}
