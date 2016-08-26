package com.afterapps.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afterapps.movies.R;
import com.afterapps.movies.datamodel.RealmObjectVideo;
import com.afterapps.movies.fragments.FragmentDetails;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/*
 * Created by Mahmoud.AlyuDeen on 7/12/2016.
 */
public class VideosRecyclerAdapter extends RealmRecyclerViewAdapter<RealmObjectVideo, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_WITH_TITLE = 2;
    Context context;
    FragmentDetails fragment;

    public VideosRecyclerAdapter(Context context, OrderedRealmCollection<RealmObjectVideo> data, FragmentDetails fragment) {
        super(context, data, true);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        if (viewType == VIEW_TYPE_DEFAULT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_with_title, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_WITH_TITLE;
        else
            return VIEW_TYPE_DEFAULT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //get the current News Item
        RealmObjectVideo video = new RealmObjectVideo();
        if (getData() != null)
            video = getData().get(position);

        //setting the items' views' values
        ViewHolder holder = (ViewHolder) viewHolder;

        Picasso.with(context)
                .load(String.format(context.getString(R.string.youtube_thumbnail_format), video.getKey()))
                .placeholder(R.drawable.youtube_placeholder)
                .into(holder.thumbnailImageView);

        holder.titleTextView.setText(video.getName());

    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView thumbnailImageView;
        TextView titleTextView;
        View share;
        View clickReceiver;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            clickReceiver = itemView.findViewById(R.id.item_video_click_receiver);
            share = itemView.findViewById(R.id.share_action_image_view);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getData() != null)
                        fragment.handleVideoShare(getData().get(getLayoutPosition()));
                }
            });
            clickReceiver.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getData() != null)
                fragment.handleVideoKey(getData().get(getLayoutPosition()).getKey());
        }
    }
}