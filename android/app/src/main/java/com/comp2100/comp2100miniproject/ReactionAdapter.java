package com.comp2100.comp2100miniproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dao.UserDAO;
import dao.model.Post;
import dao.model.User;
import reactions.ReactionDisplayTag;
import reactions.ReactionType;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ViewHolder> {
    private final List<ReactionDisplayTag> localDataSet;

    public ReactionAdapter(List<ReactionDisplayTag> dataSet) {
        this.localDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView reactionCount;
        private final ImageView reactionType;

        public ViewHolder(View view) {
            super(view);
            reactionCount = view.findViewById(R.id.countView);
            reactionType = view.findViewById(R.id.typeImageView);
        }

        public void display(ReactionDisplayTag reaction) {
            reactionCount.setText(reaction.label());
            // Display the emoji
            int imageResource = switch (reaction.type()) {
                case LIKE -> R.drawable.like;
                case HAPPY -> R.drawable.happy;
                case SURPRISE -> R.drawable.surprise;
                case ANGRY -> R.drawable.angry;
                case LAUGH -> R.drawable.laugh;
                case SAD -> R.drawable.sad;
                case LOVE -> R.drawable.love;
                case GOOD_LUCK -> R.drawable.good_luck;
                case CONGRATULATIONS -> R.drawable.congratulations;
                default -> 0;
            };
            reactionType.setImageResource(imageResource);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_reaction, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReactionDisplayTag reaction = localDataSet.get(position);
        viewHolder.display(reaction);

        // Add some reaction: TODO
        viewHolder.itemView.setOnClickListener(v -> {
//            Context context = v.getContext();
//            Intent intent = new Intent(context, PostViewerActivity.class);
//            intent.putExtra("post_uuid", post.getUUID().toString());
//            context.startActivity(intent);
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
