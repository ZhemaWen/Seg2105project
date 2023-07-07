package com.example.seg2105project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TopicList extends ArrayAdapter<Topic> {

    private Activity context;
    private List<Topic> topics;

    public TopicList(Activity context, List<Topic> topics) {
        super(context, R.layout.layout_topics, topics);
        this.context = context;
        this.topics = topics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_topics, null, true);

        TextView textViewTopicName = listViewItem.findViewById(R.id.topicName);


        Topic topic = topics.get(position);
        textViewTopicName.setText(topic.getTopicName());


        return listViewItem;
    }
}
