package com.comp2100.comp2100miniproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Post;
import dao.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static List<Post> posts;
    private RecyclerView recycler;
    private int numPosts = 8;
    public static boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(darkMode ? R.style.Theme_Dark : R.style.Theme_Light);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dark mode button
        Button darkModeButton = findViewById(R.id.btnDark);
        darkModeButton.setOnClickListener(v -> {
            darkMode = !darkMode;
            recreate();
        });
        darkModeButton.setText(darkMode ? "Dark Mode ON" : "Dark Mode OFF");

        // Get recycler view
        recycler = findViewById(R.id.recyclerPosts);

        // Display posts in two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        // Fill the posts
        if (posts == null) {
            posts = new ArrayList<>();
            for (int i = 0; i < numPosts; i++) {
                Post post = PostDAO.getInstance().getRandom();
                posts.add(post);
            }
        }

        // Connect to the adapter
        PostAdapter adapter = new PostAdapter(posts);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}