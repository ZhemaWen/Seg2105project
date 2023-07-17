package com.example.seg2105project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Requestlist extends ArrayAdapter<Request> {

    private Activity context;
    private List<Request> requests;

    public Requestlist(Activity context, List<Request> requests) {
        super(context, R.layout.layout_requests, requests);
        this.context = context;
        this.requests = requests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_requests, null, true);

        TextView textViewRequestId = listViewItem.findViewById(R.id.textViewRequests);
        TextView textViewStatus = listViewItem.findViewById(R.id.textViewStatus);

        Request request = requests.get(position);
        textViewRequestId.setText("Date: "+request.getDate());
        textViewStatus.setText("Status: "+request.getStatus());

        return listViewItem;
    }

    boolean isRequestAdded(Request request) {
        if (requests.contains(request)) {
            return true;
        }
        return false;
    }

    boolean isRequestDeleted(Request request) {
        if (requests.contains(request)) {
            return false;
        }
        return true;
    }
}
