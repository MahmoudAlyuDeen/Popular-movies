package com.afterapps.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afterapps.movies.R;
import com.afterapps.movies.datamodel.RealmObjectReview;
import com.afterapps.movies.fragments.FragmentDetails;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/*
 * Created by Mahmoud.AlyuDeen on 7/12/2016.
 */
public class ReviewsRecyclerAdapter extends RealmRecyclerViewAdapter<RealmObjectReview, RecyclerView.ViewHolder> {

    Context context;
    FragmentDetails fragment;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_WITH_TITLE = 2;

    public ReviewsRecyclerAdapter(Context context, OrderedRealmCollection<RealmObjectReview> data, FragmentDetails fragment) {
        super(context, data, true);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        if (viewType == VIEW_TYPE_DEFAULT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_with_title, parent, false);
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
        RealmObjectReview review = new RealmObjectReview();
        if (getData() != null)
            review = getData().get(position);

        //setting the items' views' values
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.authorTextView.setText(review.getAuthor());
        holder.bodyTextView.setText(review.getContent());

    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView authorTextView;
        public TextView bodyTextView;
        View clickReceiver;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            authorTextView = (TextView) itemView.findViewById(R.id.review_author_text_view);
            bodyTextView = (TextView) itemView.findViewById(R.id.review_body_text_view);
            clickReceiver = itemView.findViewById(R.id.item_review_click_receiver);
            clickReceiver.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getData() != null)
            fragment.handleReviewUri(getData().get(getLayoutPosition()).getUrl());
        }
    }
}