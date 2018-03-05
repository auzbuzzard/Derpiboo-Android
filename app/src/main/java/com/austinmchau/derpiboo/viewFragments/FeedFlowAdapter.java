package com.austinmchau.derpiboo.viewFragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.austinmchau.derpiboo.R;
import com.austinmchau.derpiboo.dbbackend.DBImage;
import com.austinmchau.derpiboo.dbbackend.VolleyRequestQueue;

import java.util.ArrayList;

/**
 * Created by Austin on 4/2/16.
 */
public class FeedFlowAdapter extends RecyclerView.Adapter<FeedFlowAdapter.FeedFlowViewHolder> {

    ArrayList<DBImage> dataset;
    ImageLoader imageLoader;
    Context context;

    public FeedFlowAdapter(ArrayList<DBImage> dataset) {
        this.dataset = dataset;
    }

    @Override
    public FeedFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_flow_card, parent, false);
        context = parent.getContext();
        imageLoader = VolleyRequestQueue.getInstance(parent.getContext()).getImageLoader();
        return new FeedFlowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedFlowViewHolder holder, int position) {
        DBImage dbImage = dataset.get(position);

        holder.cardTitle.setText(Integer.toString(dbImage.getId_number()));

        holder.cardImage.setImageUrl(dbImage.thumbURL, imageLoader);
        holder.cardImage.requestLayout();
    }




    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class FeedFlowViewHolder extends RecyclerView.ViewHolder {

        protected TextView cardTitle;
        protected NetworkImageView cardImage;
        protected TextView cardFooter;

        public FeedFlowViewHolder(View itemView) {
            super(itemView);

            cardTitle = (TextView) itemView.findViewById(R.id.card_view_title);
            cardImage = (NetworkImageView) itemView.findViewById(R.id.card_view_image);
            cardFooter = (TextView) itemView.findViewById(R.id.card_view_bottom_text);
        }
    }


}
