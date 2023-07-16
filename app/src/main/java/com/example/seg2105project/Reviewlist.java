package com.example.seg2105project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Reviewlist extends ArrayAdapter<Review> {

    private Activity context;
    private List<Review> reviews;

    public Reviewlist(Activity context, List<Review> reviews) {
        super(context, R.layout.layout_reviews, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_reviews, null, true);

        TextView textViewReview = listViewItem.findViewById(R.id.textViewReview);
        TextView textViewReviewName = listViewItem.findViewById(R.id.textViewName);


        Review review = reviews.get(position);

        textViewReview.setText("Date: " + review.getDate());
        textViewReviewName.setText("Topic: " + review.getRequest().getTopic().getTopicName());

        return listViewItem;
    }

    boolean isReviewAdded(Review review) {
        return reviews.contains(review);
    }

    boolean isReviewDeleted(Review review) {
        return !reviews.contains(review);
    }
}
