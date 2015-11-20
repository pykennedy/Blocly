package com.example.peter.blocly.api;

import android.util.Log;

import com.example.peter.blocly.BloclyApplication;
import com.example.peter.blocly.R;
import com.example.peter.blocly.api.model.RssFeed;
import com.example.peter.blocly.api.model.RssItem;
import com.example.peter.blocly.api.network.GetFeedsNetworkRequest;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

    private List<RssFeed> feeds;
    private List<RssItem> items;

    public DataSource() {
        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();
        createFakeData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                new GetFeedsNetworkRequest("http://spendyourleapsecondhere.com/").performRequest();
            }
        }).start();
        Log.v("Number of items: ", Integer.toString(feeds.size()));
        for (RssFeed rfItems : feeds) {
            Log.v("The Title: ", rfItems.getTitle());
        }
        for (RssFeed rfItems : feeds) {
            Log.v("The URL: ", rfItems.getSiteUrl());
        }
    }

    public List<RssFeed> getFeeds() {
        return feeds;
    }

    public List<RssItem> getItems() {
        return items;
    }

    void createFakeData() {
        feeds.add(new RssFeed("My Favorite Feed",
                "This feed is just incredible, I can't even begin to tell you…",
                "http://favoritefeed.net", "http://feeds.feedburner.com/favorite_feed?format=xml"));
        for (int i = 0; i < 10; i++) {
            items.add(new RssItem(String.valueOf(i),
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_headline) + " " + i,
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_content),
                    "http://spendyourleapsecondhere.com/",
                    "https://i.ytimg.com/vi/pbS--riCP8w/hqdefault.jpg",
                    0, System.currentTimeMillis(), false, false));
        }
    }
}