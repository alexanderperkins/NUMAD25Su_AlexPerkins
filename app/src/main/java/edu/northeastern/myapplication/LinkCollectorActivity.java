package edu.northeastern.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LinkCollectorActivity extends AppCompatActivity implements LinkAdapter.OnLinkInteractionListener {

    private static final String KEY_LINK_LIST = "link_list";
    private ArrayList<LinkItem> linkList;
    private LinkAdapter linkAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_collector);

        // This makes sure content isn't hidden by phone status/nav bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        fabAddLink = findViewById(R.id.fabAddLink);

        // Student: Restore links if screen rotated
        if (savedInstanceState != null) {
            linkList = savedInstanceState.getParcelableArrayList(KEY_LINK_LIST);
        }
        if (linkList == null) {
            linkList = new ArrayList<>();
        }

        // Student: Set up the list view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linkAdapter = new LinkAdapter(this, linkList, this);
        recyclerView.setAdapter(linkAdapter);

        // Student: What happens when the add button is tapped
        fabAddLink.setOnClickListener(v -> showAddEditLinkDialog(-1));
    }

    // Student: Save links when the screen rotates
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_LINK_LIST, linkList);
    }

    // Student: What happens when a link is tapped in the list
    @Override
    public void onLinkClick(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url; // Add http if missing
        }
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            // Student: Show error if browser can't open
            Snackbar.make(recyclerView, "Can't open URL: " + url, Snackbar.LENGTH_LONG).show();
        }
    }

    // Student: What happens when the edit button is tapped
    @Override
    public void onLinkEdit(int position) {
        showAddEditLinkDialog(position);
    }

    // Student: What happens when the delete button is tapped
    @Override
    public void onLinkDelete(int position) {
        if (position != RecyclerView.NO_POSITION) {
            LinkItem deletedLink = linkList.remove(position);
            linkAdapter.notifyItemRemoved(position);

            Snackbar snackbar = Snackbar.make(recyclerView, "Link '" + deletedLink.getName() + "' deleted.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linkList.add(position, deletedLink);
                    linkAdapter.notifyItemInserted(position);
                    Snackbar.make(recyclerView, "Link restored.", Snackbar.LENGTH_SHORT).show();
                }
            });
            snackbar.show();
        }
    }

    // Student: Shows a pop-up to add or edit a link
    private void showAddEditLinkDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_link, null);
        builder.setView(dialogView);

        EditText inputName = dialogView.findViewById(R.id.editLinkName);
        EditText inputUrl = dialogView.findViewById(R.id.editLinkUrl);

        // If editing, fill in current link details
        if (position != -1) {
            LinkItem itemToEdit = linkList.get(position);
            inputName.setText(itemToEdit.getName());
            inputUrl.setText(itemToEdit.getUrl());
            builder.setTitle("Edit Link");
        } else {
            builder.setTitle("Add New Link");
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String url = inputUrl.getText().toString().trim();

            if (name.isEmpty() || url.isEmpty()) {
                Snackbar.make(recyclerView, "Name and URL can't be empty.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (position != -1) {
                // Student: Update existing link
                linkList.get(position).setName(name);
                linkList.get(position).setUrl(url);
                linkAdapter.notifyItemChanged(position);
                Snackbar.make(recyclerView, "Link updated!", Snackbar.LENGTH_LONG).show();
            } else {
                // Student: Add a new link
                linkList.add(new LinkItem(name, url));
                linkAdapter.notifyItemInserted(linkList.size() - 1);
                Snackbar snackbar = Snackbar.make(recyclerView, "Link added!", Snackbar.LENGTH_LONG);
                snackbar.setAction("VIEW", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLinkClick(url);
                    }
                });
                snackbar.show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}