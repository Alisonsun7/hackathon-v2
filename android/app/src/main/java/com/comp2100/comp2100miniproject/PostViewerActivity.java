package com.comp2100.comp2100miniproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PostViewerActivity extends AppCompatActivity {
    private List<Message> messages;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.PostViewer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /* Part 2 & 3 */
        // Set Post Title
        TextView titleText = findViewById(R.id.postTitle);
        titleText.setText("Android studio works!");

        // 1. Generate random data
//        RandomContentGenerator.populateRandomData();

        // 2. Get a random post
        Post post = PostDAO.getInstance().getRandom();

        /* Part 4 */
        // Receive data
        String postId = getIntent().getStringExtra("post_uuid");
        if (postId != null) {
            UUID uuid = UUID.fromString(postId);
            post = PostDAO.getInstance().getByUUID(uuid);
        }

        // 3. Get its author
        assert UserDAO.getInstance() != null;
        User author = UserDAO.getInstance().getByUUID(post.poster);

        // 4. Display contents in TextView
        TextView authorText = findViewById(R.id.postAuthor);

        if (author != null) {
            titleText.setText(post.topic);
            authorText.setText(author.username());
        } else {
            titleText.setText("No post available");
            authorText.setText("Unknown");
        }

        // Get Recycler view
        recycler = findViewById(R.id.recyclerReplies);

        // Fill the messages
        messages = new ArrayList<>();
        Iterator<Message> it = post.messages.getAll();
        while (it.hasNext()) {
            messages.add(it.next());
        }
        TextView msgCountText = findViewById(R.id.postMsgCount);
        msgCountText.setText(messages.size() + " replies");

        // Set back button
        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Connect to the adapter
        MessageAdapter adapter = new MessageAdapter(messages);
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}