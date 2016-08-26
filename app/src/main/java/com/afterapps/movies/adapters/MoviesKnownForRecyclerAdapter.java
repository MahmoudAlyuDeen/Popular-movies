package com.afterapps.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afterapps.movies.R;
import com.afterapps.movies.datamodel.KnownFor;
import com.squareup.picasso.Picasso;

import java.util.List;


/*
 * Created by Mahmoud.AlyuDeen on 7/12/2016.
 */
public class MoviesKnownForRecyclerAdapter extends RecyclerView.Adapter {

    Context context;
    List<KnownFor> movies;

    public MoviesKnownForRecyclerAdapter(Context context, List<KnownFor> movies) {
        super();
        this.context = context;
        this.movies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_known_for, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //get the current movie
        KnownFor movie = movies.get(position);

        //setting the items' views' values
        ViewHolder holder = (ViewHolder) viewHolder;

        final String profileImageUrl = String.format(context.getString(R.string.image_url_format_w500)
                , movie.getPosterPath());

        Picasso.with(context)
                .load(profileImageUrl)
                .placeholder(R.drawable.poster_placeholder_smaller)
                .fit()
                .centerCrop()
                .into(holder.knownForImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView knownForImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            knownForImageView = (ImageView) itemView.findViewById(R.id.movie_poster_known_for_image_view);
        }

    }
}