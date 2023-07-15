package com.example.seg2105project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class topicAdapter extends RecyclerView.Adapter<topicAdapter.ViewHolder>{

        private List<Topic> topic;

        public topicAdapter(List<Topic> topic){
            this.topic = topic;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_search_lesson, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Topic topics = topic.get(position);
            // Bind the topic data to the views in the item layout
            holder.titleTextView.setText(topics.getTopicName());
            holder.tutorTextView.setText(topics.getTutorId());
        }

        @Override
        public int getItemCount() {
            return topic.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView tutorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            tutorTextView = itemView.findViewById(R.id.tutorTextView);
        }
    }
    }
