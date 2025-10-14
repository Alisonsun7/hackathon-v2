package com.comp2100.comp2100miniproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dao.UserDAO;
import dao.model.Message;
import dao.model.TimestampFormatter;
import dao.model.TimestampFormatterTimeSinceEnglish;
import dao.model.User;
import reactions.IReactionReporter;
import reactions.ReactionDisplayTag;
import reactions.ReactionReportFactory;
import reactions.ReactionType;
import reactions.ReactionsFacade;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final List<Message> localDataSet;

    public MessageAdapter(List<Message> dataSet) {
        this.localDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MessageAdapter messageAdapter;
        private final TextView msgAuthor;
        private final TextView msgTimestamp;
        private final TextView msgContent;
        private final RecyclerView msgReactions;
        private final Button btnReact;
        private final TimestampFormatter timeFormatter = new TimestampFormatterTimeSinceEnglish();

        public ViewHolder(View view, MessageAdapter adapter) {
            super(view);
            msgAuthor = view.findViewById(R.id.msgAuthor);
            msgTimestamp = view.findViewById(R.id.msgTimestamp);
            msgContent = view.findViewById(R.id.msgContent);
            msgReactions = view.findViewById(R.id.recyclerReactions);
            btnReact = view.findViewById(R.id.btnReact);
            messageAdapter = adapter;
        }

        public void display(Message message) {
            // Display basic message info
            User author = UserDAO.getInstance().getByUUID(message.poster());
            if (author != null) {
                msgAuthor.setText(author.username());
            } else {
                msgAuthor.setText("Unknown");
            }

            TimestampFormatterTimeSinceEnglish timeFormatter = new TimestampFormatterTimeSinceEnglish();

            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(new Date(message.timestamp()));
            msgTimestamp.setText(timeFormatter.format(message.timestamp()));

            msgContent.setText(message.message());

            // Display reactions
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 3);

            // Fill the reactions
            IReactionReporter reactionReporter = ReactionReportFactory.buildReporter("overview");
            List<ReactionDisplayTag> reactions = Arrays.asList(reactionReporter.generateReport(message));

            // Connect to the adapter
            ReactionAdapter adapter = new ReactionAdapter(reactions);
            msgReactions.setLayoutManager(gridLayoutManager);
            msgReactions.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // Bind React button
            btnReact.setOnClickListener(v -> showReactionPopup(v, adapter, message));
        }

        private void showReactionPopup(View anchorView, ReactionAdapter adapter, Message message) {
            View popupView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.popup_reactions, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight() * 2);

            popupView.findViewById(R.id.btnLike).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.LIKE,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnHappy).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.HAPPY,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnSurprise).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.SURPRISE,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnAngry).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.ANGRY,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnLaugh).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.LAUGH,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnSad).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.SAD,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnLove).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.LOVE,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnGoodLuck).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.GOOD_LUCK,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.btnCongratulations).setOnClickListener(v -> {
                ReactionsFacade.addReaction(
                        MainActivity.currentUser.getUUID(), message.id(),
                        ReactionType.CONGRATULATIONS,
                        System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                messageAdapter.notifyItemChanged(getAdapterPosition());
                popupWindow.dismiss();
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_message, viewGroup, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.display(localDataSet.get(position));

        viewHolder.itemView.setOnClickListener(v -> {
            onClickListener.onClick(position, localDataSet.get(position));
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
        void onClick(int i, Message message);
    }
}
