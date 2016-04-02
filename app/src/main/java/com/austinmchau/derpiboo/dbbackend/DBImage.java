package com.austinmchau.derpiboo.dbbackend;

import android.media.Image;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Austin on 4/1/16.
 */
public class DBImage {

    public final static String TAG = "Derpiboo";

    public static class Representations {
        URL thumb_tiny;
        URL thumb_small;
        URL thumb;
        URL small;
        URL medium;
        URL large;
        URL tall;
        URL full;
    }

    //
    //Mark: Data Fields
    //

    int id_number; //Essential
    URL imageURL; //Essential
    Representations representations; //Essential
    public URL thumbnail() {
        return representations.thumb;
    }

    DBImage(int id_number, String imageURLString) {
        this.id_number = id_number;
        try {
            this.imageURL = new URL("http:" + imageURLString);
        } catch (MalformedURLException error) {
            Log.d(TAG, error.toString());
        }
    }

    //
    //Mark: Data Store
    //

    Image image;
    public static class RepresentationsImage {
        Image thumb_tiny;
        Image thumb_small;
        Image thumb;
        Image small;
        Image medium;
        Image large;
        Image tall;
        Image full;
    }

}
