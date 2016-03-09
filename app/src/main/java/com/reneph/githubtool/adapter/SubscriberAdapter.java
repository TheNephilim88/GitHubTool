package com.reneph.githubtool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.reneph.githubtool.R;
import com.reneph.githubtool.data.UserData;
import com.reneph.githubtool.util.GitHubClient;

import java.util.List;

/**
 * Created by Robert on 09.03.2016.
 */
public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.ViewHolder> {
    private final Context mCtx;
    private final List<UserData> items;

    public SubscriberAdapter(List<UserData> data, Context context) {
        items = data;
        mCtx = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(items.get(position) != null) {
            holder.mSubscriberName.setText(String.format(mCtx.getString(R.string.subscriber_name), items.get(position).getName()));

            if(!items.get(position).getAvatarUrl().equals("")) {
                ImageLoader imageLoader = GitHubClient.getInstance(mCtx).getImageLoader();
                holder.mAvatar.setImageUrl(items.get(position).getAvatarUrl(), imageLoader);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(items == null){
            return 0;
        }

        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final NetworkImageView mAvatar;
        public final TextView mSubscriberName;

        public ViewHolder(View itemView) {
            super(itemView);

            mAvatar = (NetworkImageView) itemView.findViewById(R.id.avatar);
            mAvatar.setErrorImageResId(R.drawable.avatar_placeholder_error);
            mAvatar.setDefaultImageResId(R.drawable.avatar_placeholder);

            mSubscriberName = (TextView) itemView.findViewById(R.id.subscriber_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscriber_list, parent, false);

        return  new ViewHolder(v);
    }
}
