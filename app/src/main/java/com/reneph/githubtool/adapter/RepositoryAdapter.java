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
import com.reneph.githubtool.data.RepositoryData;
import com.reneph.githubtool.util.GitHubClient;

import java.util.List;

/**
 * Created by Robert on 09.03.2016.
 */
public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<RepositoryData> items;

    public RepositoryAdapter(List<RepositoryData> data, Context context) {
        items = data;
        mCtx = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(items.get(position) != null) {
            holder.mRepositoryName.setText(String.format(mCtx.getString(R.string.repo_name), items.get(position).getRepositoryName()));
            holder.mRepositoryDescription.setText(String.format(mCtx.getString(R.string.repo_description), items.get(position).getRepositoryDescription()));
            holder.mRepositoryForks.setText(String.format(mCtx.getString(R.string.repo_forks), items.get(position).getForks()));

            if(!items.get(position).getImageURI().equals("")) {
                ImageLoader imageLoader = GitHubClient.getInstance(mCtx).getImageLoader();
                holder.mOwnerAvatar.setImageUrl(items.get(position).getImageURI(), imageLoader);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public NetworkImageView mOwnerAvatar;
        public TextView mRepositoryName;
        public TextView mRepositoryDescription;
        public TextView mRepositoryForks;

        public ViewHolder(View itemView) {
            super(itemView);

            mOwnerAvatar = (NetworkImageView) itemView.findViewById(R.id.owner_avatar);
            mOwnerAvatar.setErrorImageResId(R.drawable.avatar_placeholder_error);
            mOwnerAvatar.setDefaultImageResId(R.drawable.avatar_placeholder);

            mRepositoryName = (TextView) itemView.findViewById(R.id.repository_name);
            mRepositoryDescription = (TextView) itemView.findViewById(R.id.repository_description);
            mRepositoryForks = (TextView) itemView.findViewById(R.id.repository_forks);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repository_list, parent, false);

        return  new ViewHolder(v);
    }
}
