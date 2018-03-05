package com.austinmchau.derpiboo.viewFragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.austinmchau.derpiboo.R;
import com.austinmchau.derpiboo.dbbackend.CompletionHandler;
import com.austinmchau.derpiboo.dbbackend.DBImage;
import com.austinmchau.derpiboo.dbbackend.Derpibooru;

import java.util.ArrayList;


public class FeedFlowFragment extends Fragment {

    Derpibooru.QueryMode queryMode = Derpibooru.QueryMode.images;

    Derpibooru derpibooru = new Derpibooru(queryMode);
    protected ArrayList<DBImage> imagelist() {
        return derpibooru.dataset;
    }

    private static View view;
    private static RelativeLayout refreshLayout;
    private static LinearLayoutManager layoutManager;
    private static FeedFlowAdapter adapter;
    private static RecyclerView recyclerView;

    // Variables for scroll listener
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed_flow, container, false);

        refreshLayout = (RelativeLayout) view.findViewById(R.id.load_more_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.cardList);


        //recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FeedFlowAdapter(imagelist());

        recyclerView.setAdapter(adapter);

        derpibooru.setImageDownloadCompletionHandler(new CompletionHandler() {
            @Override
            public void handle(String reason) {
                adapter.notifyDataSetChanged();
            }
        });

        downloadMoreImages();
        implementScrollListener();

        return view;
    }

    //
    // Mark: Data Management
    //

    public void downloadNewImages(String query) {
        derpibooru.searchTerm = query;
        derpibooru.clearDataSource();
        downloadMoreImages();
    }

    public void downloadMoreImages() {
        if (checkNetworkConnection()) {
            derpibooru.loadMoreImages();
        }
    }

    //
    //Mark: Private Methods
    //

    private boolean checkNetworkConnection() {

        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void implementScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // If scroll state is touch scroll then set userScrolled
                // true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,
                                   int dy) {

                super.onScrolled(recyclerView, dx, dy);
                // Here get the child count, item count and visibleitems
                // from layout manager

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                // Now check if userScrolled is true and also check if
                // the item is end then update recycler view and set
                // userScrolled to false
                if (userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;

                    updateRecyclerView();
                }

            }
        });
    }

    // Method for repopulating recycler view
    private void updateRecyclerView() {

        // Show Progress Layout
        refreshLayout.setVisibility(View.VISIBLE);

        // Handler to show refresh for a period of time you can use async task
        // while commnunicating serve

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                downloadMoreImages();

                adapter.notifyDataSetChanged();// notify adapter

                // Toast for task completion
                Toast.makeText(getActivity(), "Items Updated.",
                        Toast.LENGTH_SHORT).show();

                // After adding new data hide the view.
                refreshLayout.setVisibility(View.GONE);

            }
        }, 5000);
    }

 }
