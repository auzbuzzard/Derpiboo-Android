package com.austinmchau.derpiboo.dbbackend;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class DBImage {

    public final static String TAG = "Derpiboo";
    public final static String httpType = "http:";

    public static class Representations {
        String thumb_tiny;
        String thumb_small;
        String thumb;
        String small;
        String medium;
        String large;
        String tall;
        String full;


    }

    //
    //Mark: Data Fields
    //

    int id_number; //Essential
    String imageURL; //Essential
    Representations representations; //Essential
    public String thumbURL;
    public String thumbnail() {
        return representations.thumb;
    }

    DBImage(int id_number, String imageURLString, Representations representations) {
        this.id_number = id_number;
        this.imageURL = imageURLString;
        this.representations = representations;
    }

    //Getter
    public int getId_number() {
        return  id_number;
    }

    //
    //Mark: Data Store
    //

    Bitmap image; // Full Image

    public static class RepresentationsImage {
        Bitmap thumb_tiny;
        Bitmap thumb_small;
        Bitmap thumb;
        Bitmap small;
        Bitmap medium;
        Bitmap large;
        Bitmap tall;
        Bitmap full;
    }

    RepresentationsImage representationsImage = new RepresentationsImage();

    public Bitmap thumbImage() {
        return representationsImage.thumb;
    }

    public void setThumbImage(Bitmap image) {
        representationsImage.thumb = image;
    }

    //
    //Mark: public methods
    //

    public void downloadThumb(Context context, final DBImageBitmapListener listener) {
        if (thumbImage() == null) {
            ImageRequest request = new ImageRequest(thumbnail(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            setThumbImage(response);
                            listener.onLoadingComplete(DBImage.this);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
        }
    }

    //
    //Mark: Convenience
    //
    public static URL stringToUrl(String url) {
        URL u = null;
        try {
            new URL(httpType + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return u;
    }


}
