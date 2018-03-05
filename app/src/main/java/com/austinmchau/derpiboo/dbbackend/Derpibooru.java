package com.austinmchau.derpiboo.dbbackend;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Derpibooru implements DerpibooruDataSource {

    private static final String DEBUG_TAG = "DerpibooruClass";

    public enum QueryMode { images, search }

    //
    //Mark: Android shenanigans
    //

    //
    //Mark: Data Store / properties
    //

    public ArrayList<DBImage> dataset = new ArrayList<>();
    public int imagesPerPage = 10;
    public int currentPage() {
        return dataset.size() / imagesPerPage + 1;
    }

    public String searchTerm = "";

    public QueryMode queryMode;

    public Derpibooru(QueryMode queryMode) {
        this.queryMode = queryMode;
    }

    private String apiKey = "2tuq4sWz8pd7sy6f2MA3";
    private Boolean useAPIKey = false;

    //Getters


    //
    //Mark: Access methods
    //

    @Override
    public ArrayList<DBImage> getImageArray() {
        return dataset;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public void setSearchTerm(String query) {
        searchTerm = query;
    }

    @Override
    public void clearDataSource() {
        dataset.clear();
    }

    @Override
    public void loadNewImages() {
        clearDataSource();
        loadMoreImages();
    }

    @Override
    public void loadMoreImages() {
        String urlString = "https://derpibooru.org/";
        if (!useAPIKey) {
            queryMode = QueryMode.search;
        }
        urlString += queryMode.name() + ".json";
        urlString += "?" + "page=" + currentPage();
        urlString += "&" + "perpage=" + imagesPerPage;
        //login info using user API key
        if (useAPIKey) {
            urlString += "&" + "key=" + apiKey;
        } else {

        }

        //search terms
        if (searchTerm == "") {
            if (!useAPIKey) {
                searchTerm += ", safe";
            }
            try {
                urlString += "&" + "q=" + URLEncoder.encode(searchTerm, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {

        }

        Log.d("tag", urlString);

        //Log.d("tag", urlString);
        try {
            URL url = new URL(urlString);
            new DownloadJsonTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    CompletionHandler savedImageDownloadHandler;
    public void setImageDownloadCompletionHandler(CompletionHandler h) {
        savedImageDownloadHandler = h;
    }


    //
    //Mark: Private Methods
    //

    private class DownloadJsonTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {

            // params comes from the execute() call: params[0] is the url.
            return downloadJSON(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            parseJson(dataset, jsonObject);
            //Log.d("tag", "db" + dataset.toString());
            savedImageDownloadHandler.handle("JSON task download Completed.");
        }
    }

    private JSONObject downloadJSON(URL url) {
        InputStream is = null;
        JSONObject JSONObject = null;

        //HTTP connection
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);

            if (response == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    result.append(line + "\n");
                }
                r.close();

                JSONObject = new JSONObject(result.toString());
            } else {
                return new JSONObject();
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONObject;
    }

    private void parseJson(ArrayList<DBImage> array, JSONObject jObj) {
        try {
            JSONArray result = jObj.getJSONArray(queryMode.name());

            DBImage dbImage;
            DBImage.Representations representations = new DBImage.Representations();

            for (int i = 0; i < result.length(); i++) {
                JSONObject item = result.getJSONObject(i);

                int id_number = item.getInt("id_number");
                String imageURLString = item.getString("image");

                JSONObject rep = item.getJSONObject("representations");
                //representation partial implementation
                String thumbURL = rep.getString("thumb");
                representations.thumb = "https:" + thumbURL;

                //DBImage init
                dbImage = new DBImage(id_number, imageURLString, representations);
                dbImage.thumbURL = "https:" + thumbURL;
                //Add dbimage to array
                array.add(dbImage);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

}
