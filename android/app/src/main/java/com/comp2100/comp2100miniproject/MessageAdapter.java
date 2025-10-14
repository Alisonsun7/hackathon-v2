package com.comp2100.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dao.UserDAO;
import dao.model.Message;
import dao.model.TimestampFormatterTimeSinceEnglish;
import dao.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final List<Message> localDataSet;

    public MessageAdapter(List<Message> dataSet) {
        this.localDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView msgAuthor;
        private final TextView msgTimestamp;
        private final TextView msgContent;

        public ViewHolder(View view) {
            super(view);
            msgAuthor = view.findViewById(R.id.msgAuthor);
            msgTimestamp = view.findViewById(R.id.msgTimestamp);
            msgContent = view.findViewById(R.id.msgContent);
        }

        public void display(Message message) {
            User author = UserDAO.getInstance().getByUUID(message.poster());
            if (author != null) {
                msgAuthor.setText(author.username());
            } else {
                msgAuthor.setText("Unknown");
            }

            TimestampFormatterTimeSinceEnglish timeFormatter = new TimestampFormatterTimeSinceEnglish();

            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(new Date(message.timestamp()));
            msgTimestamp.setText(time);

            msgContent.setText(message.message());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_message, viewGroup, false);
        return new ViewHolder(view);
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
