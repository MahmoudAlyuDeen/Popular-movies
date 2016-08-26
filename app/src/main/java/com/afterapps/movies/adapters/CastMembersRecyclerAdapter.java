package com.afterapps.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afterapps.movies.R;
import com.afterapps.movies.datamodel.RealmObjectCastMember;
import com.afterapps.movies.fragments.FragmentDetails;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/*
 * Created by Mahmoud.AlyuDeen on 7/12/2016.
 */
public class CastMembersRecyclerAdapter extends RealmRecyclerViewAdapter<RealmObjectCastMember, RecyclerView.ViewHolder> {

    Context context;
    FragmentDetails fragment;

    public CastMembersRecyclerAdapter(Context context
            , OrderedRealmCollection<RealmObjectCastMember> data
            , FragmentDetails fragment) {
        super(context, data, true);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast_member, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //get the current cast member
        RealmObjectCastMember realmObjectCastMember = new RealmObjectCastMember();
        if (getData() != null)
            realmObjectCastMember = getData().get(position);

        //setting the items' views' values
        ViewHolder holder = (ViewHolder) viewHolder;

        final String profileImageUrl = String.format(context.getString(R.string.image_url_format_w500)
                , realmObjectCastMember.getProfilePath());
        Picasso.with(context)
                .load(profileImageUrl)
                .placeholder(R.drawable.cast_member_place_holder)
                .fit()
                .centerCrop()
                .into(holder.castMemberProfileImageView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public ImageView castMemberProfileImageView;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            castMemberProfileImageView = (ImageView) itemView.findViewById(R.id.item_cast_member_image_view);
        }

        @Override
        public void onClick(View v) {
            //noinspection ConstantConditions
            fragment.handleCastRecyclerClick(getData().get(getLayoutPosition()).getId());
        }
    }
}