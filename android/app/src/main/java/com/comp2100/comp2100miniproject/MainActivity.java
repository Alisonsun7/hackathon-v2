package com.comp2100.comp2100miniproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.model.Post;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Post> posts;
    private RecyclerView recycler;
    private int numPosts = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Generate random data
        RandomContentGenerator.populateRandomData();

        // Get recycler view
        recycler = findViewById(R.id.recyclerPosts);

        // Display posts in two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        // Fill the posts
        posts = new ArrayList<>();
        for (int i = 0; i < numPosts; i++) {
            Post post = PostDAO.getInstance().getRandom();
            posts.add(post);
        }

        // Connect to the adapter
        PostAdapter adapter = new PostAdapter(posts);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}