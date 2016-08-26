package com.afterapps.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afterapps.movies.R;
import com.afterapps.movies.activities.ActivityMain;
import com.afterapps.movies.datamodel.RealmObjectMovie;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/*
 * Created by Mahmoud.AlyuDeen on 7/12/2016.
 */
public class MoviesRecyclerAdapter extends RealmRecyclerViewAdapter<RealmObjectMovie, RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "posterURL";
    Context context;

    public MoviesRecyclerAdapter(Context context, OrderedRealmCollection<RealmObjectMovie> data) {
        super(context, data, true);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //get the current News Item
        RealmObjectMovie movieItem = new RealmObjectMovie();
        if (getData() != null)
            movieItem = getData().get(position);

        //setting the items' views' values
        ViewHolder holder = (ViewHolder) viewHolder;

        final String posterURL = String.format(context.getString(R.string.image_url_format_w500), movieItem.getPosterPath());
        Picasso.with(context)
                .load(posterURL)
                .placeholder(R.drawable.poster_placeholder)
                .fit()
                .centerCrop()
                .into(holder.moviePosterImageView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public ImageView moviePosterImageView;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            moviePosterImageView = (ImageView) itemView.findViewById(R.id.item_movie_poster_image_view);
        }

        @Override
        public void onClick(View v) {
            //noinspection ConstantConditions
            ((ActivityMain) context).handleRecyclerClick(getData().get(getLayoutPosition()).getId());
        }
    }
}