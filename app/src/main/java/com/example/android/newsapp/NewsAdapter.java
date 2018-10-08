package com.example.android.newsapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class NewsAdapter extends ArrayAdapter<News> {


    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    private static final String authorSeparator =" | ";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView
                    = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        News currentNewsItem = getItem(position);

        // Find the TextView with view ID title
        TextView title = convertView.findViewById(R.id.title);
        title.setText(currentNewsItem.getTitle());

        // Find the TextView with view ID section
        TextView section = convertView.findViewById(R.id.section);
        section.setText(currentNewsItem.getSection());

        // Find the TextView with view ID author
        TextView author = convertView.findViewById(R.id.author);
        if (currentNewsItem.getAuthor() != "") {
            author.setText(currentNewsItem.getAuthor());
        } else {
            author.setText(R.string.no_author);
        }
        // Find the TextView with view ID publishedDate
        TextView publishedDate = convertView.findViewById(R.id.published_date);
        publishedDate.setText(currentNewsItem.getPublishedDate());

        return convertView;
    }

}
