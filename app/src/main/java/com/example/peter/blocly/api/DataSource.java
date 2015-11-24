package com.example.peter.blocly.api;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.peter.blocly.BloclyApplication;
import com.example.peter.blocly.BuildConfig;
import com.example.peter.blocly.R;
import com.example.peter.blocly.api.model.RssFeed;
import com.example.peter.blocly.api.model.RssItem;
import com.example.peter.blocly.api.model.database.DatabaseOpenHelper;
import com.example.peter.blocly.api.model.database.table.RssFeedTable;
import com.example.peter.blocly.api.model.database.table.RssItemTable;
import com.example.peter.blocly.api.network.GetFeedsNetworkRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataSource {

    public static final String ACTION_DOWNLOAD_COMPLETED =
            DataSource.class.getCanonicalName().concat(".ACTION_DOWNLOAD_COMPLETED");

    private DatabaseOpenHelper databaseOpenHelper;
    private RssFeedTable rssFeedTable;
    private RssItemTable rssItemTable;
    private List<RssFeed> feeds;
    private List<RssItem> items;

    public DataSource() {
        rssFeedTable = new RssFeedTable();
        rssItemTable = new RssItemTable();
        databaseOpenHelper = new DatabaseOpenHelper(BloclyApplication.getSharedInstance(),
                rssFeedTable, rssItemTable);

        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG && true) {
                    BloclyApplication.getSharedInstance().deleteDatabase("blocly_db");
                }
                SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

                List<GetFeedsNetworkRequest.FeedResponse> feedResponses =
                        new GetFeedsNetworkRequest("http://www.npr.org/rss/rss.php?id=1001").performRequest();
                GetFeedsNetworkRequest.FeedResponse androidCentral = feedResponses.get(0);
                long androidCentralFeedId = new RssFeedTable.Builder()
                        .setFeedURL(androidCentral.channelFeedURL)
                        .setSiteURL(androidCentral.channelURL)
                        .setTitle(androidCentral.channelTitle)
                        .setDescription(androidCentral.channelDescription)
                        .insert(writableDatabase);

                List<RssItem> newRSSItems = new ArrayList<RssItem>();
                for (GetFeedsNetworkRequest.ItemResponse itemResponse : androidCentral.channelItems) {
                    long itemPubDate = System.currentTimeMillis();
                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                    try {
                        itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long newItemRowId = new RssItemTable.Builder()
                            .setTitle(itemResponse.itemTitle)
                            .setDescription(itemResponse.itemDescription)
                            .setEnclosure(itemResponse.itemEnclosureURL)
                            .setMIMEType(itemResponse.itemEnclosureMIMEType)
                            .setLink(itemResponse.itemURL)
                            .setGUID(itemResponse.itemGUID)
                            .setPubDate(itemPubDate)
                            .setRSSFeed(androidCentralFeedId)
                            .insert(writableDatabase);
                    Cursor itemCursor = rssItemTable.fetchRow(databaseOpenHelper.getReadableDatabase(), newItemRowId);
                    itemCursor.moveToFirst();
                    RssItem newRssItem = itemFromCursor(itemCursor);
                    newRSSItems.add(newRssItem);
                    itemCursor.close();
                }
                Cursor androidCentralCursor = rssFeedTable.fetchRow(databaseOpenHelper.getReadableDatabase(), androidCentralFeedId);
                androidCentralCursor.moveToFirst();
                RssFeed androidCentralRSSFeed = feedFromCursor(androidCentralCursor);
                androidCentralCursor.close();
                items.addAll(newRSSItems);
                feeds.add(androidCentralRSSFeed);

                BloclyApplication.getSharedInstance().sendBroadcast(new Intent(ACTION_DOWNLOAD_COMPLETED));
            }
        }).start();
    }

    public List<RssFeed> getFeeds() {
        return feeds;
    }

    public List<RssItem> getItems() {
        return items;
    }

    static RssFeed feedFromCursor(Cursor cursor) {
        return new RssFeed(RssFeedTable.getTitle(cursor), RssFeedTable.getDescription(cursor),
                RssFeedTable.getSiteURL(cursor), RssFeedTable.getFeedURL(cursor));
    }

    // #4b
    static RssItem itemFromCursor(Cursor cursor) {
        return new RssItem(RssItemTable.getGUID(cursor), RssItemTable.getTitle(cursor),
                RssItemTable.getDescription(cursor), RssItemTable.getLink(cursor),
                RssItemTable.getEnclosure(cursor), RssItemTable.getRssFeedId(cursor),
                RssItemTable.getPubDate(cursor), RssItemTable.getFavorite(cursor),
                RssItemTable.getArchived(cursor));
    }

    void createFakeData() {
        feeds.add(new RssFeed("My Favorite Feed",
                "This feed is just incredible, I can't even begin to tell you…",
                "http://favoritefeed.net", "http://www.npr.org/rss/rss.php?id=1001"));
        for (int i = 0; i < 10; i++) {
            items.add(new RssItem(String.valueOf(i),
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_headline) + " " + i,
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_content),
                    "http://www.npr.org/rss/rss.php?id=1001",
                    "https://i.ytimg.com/vi/pbS--riCP8w/hqdefault.jpg",
                    0, System.currentTimeMillis(), false, false));
        }
    }
}