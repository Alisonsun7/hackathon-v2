package com.comp2100.comp2100miniproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dao.UserDAO;
import dao.model.Post;
import dao.model.User;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final List<Post> localDataSet;

    public PostAdapter(List<Post> dataSet) {
        this.localDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView postAuthor;
        private final TextView postTimestamp;
        private final TextView postContent;

        public ViewHolder(View view) {
            super(view);
            postAuthor = view.findViewById(R.id.postAuthorFrag);
            postTimestamp = view.findViewById(R.id.postTimestampFrag);
            postContent = view.findViewById(R.id.postContentFrag);
        }

        public void display(Post post) {
            User author = UserDAO.getInstance().getByUUID(post.poster);
            if (author != null) {
                postAuthor.setText(author.username());
            } else {
                postAuthor.setText("Unknown");
            }

//            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                    .format(new Date(post.timestamp()));
//            postTimestamp.setText(time);

            postContent.setText(post.topic);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Post post = localDataSet.get(position);
        viewHolder.display(post);

        viewHolder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PostViewerActivity.class);
            intent.putExtra("post_uuid", post.getUUID().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int i, Post post);
    }
}
