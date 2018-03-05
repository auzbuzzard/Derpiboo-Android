package com.austinmchau.derpiboo.dbbackend;

import java.util.ArrayList;

/**
 * Created by Austin on 4/1/16.
 */

public interface DerpibooruDataSource {
    ArrayList<DBImage> getImageArray();
    String getSearchTerm();
    void setSearchTerm(String query);

    void clearDataSource();
    void loadNewImages();
    void loadMoreImages();

}
