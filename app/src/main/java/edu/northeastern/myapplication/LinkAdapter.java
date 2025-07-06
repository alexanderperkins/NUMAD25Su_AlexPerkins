package edu.northeastern.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {

    private ArrayList<LinkItem> linkList;
    private Context context;
    private OnLinkInteractionListener listener; // Custom listener for activity interaction

    public LinkAdapter(Context context, ArrayList<LinkItem> linkList, OnLinkInteractionListener listener) {
        this.context = context;
        this.linkList = linkList;
        this.listener = listener;
    }

    // Interface for communication with LinkCollectorActivity
    public interface OnLinkInteractionListener {
        void onLinkClick(String url);
        void onLinkEdit(int position);
        void onLinkDelete(int position);
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item_layout, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        LinkItem currentItem = linkList.get(position);
        holder.linkNameTextView.setText(currentItem.getName());
        holder.linkUrlTextView.setText(currentItem.getUrl());

        // Handle link click (launch browser)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLinkClick(currentItem.getUrl());
            }
        });

        // Handle edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLinkEdit(holder.getAdapterPosition()); // Use getAdapterPosition() for current position
            }
        });

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLinkDelete(holder.getAdapterPosition()); // Use getAdapterPosition() for current position
            }
        });
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder {
        public TextView linkNameTextView;
        public TextView linkUrlTextView;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            linkNameTextView = itemView.findViewById(R.id.linkNameTextView);
            linkUrlTextView = itemView.findViewById(R.id.linkUrlTextView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}