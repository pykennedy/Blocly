package com.example.peter.blocly.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.peter.blocly.BuildConfig;
import com.example.peter.blocly.api.model.RssFeed;
import com.example.peter.blocly.api.model.RssItem;
import com.example.peter.blocly.api.model.database.DatabaseOpenHelper;
import com.example.peter.blocly.api.model.database.table.RssFeedTable;
import com.example.peter.blocly.api.model.database.table.RssItemTable;
import com.example.peter.blocly.api.model.database.table.Table;
import com.example.peter.blocly.api.network.GetFeedsNetworkRequest;
import com.example.peter.blocly.api.network.NetworkRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSource {

    public static interface Callback<Result> {
        public void onSuccess(Result result);
        public void onError(String errorMessage);
    }

    private DatabaseOpenHelper databaseOpenHelper;
    private RssFeedTable rssFeedTable;
    private RssItemTable rssItemTable;

    private ExecutorService executorService;

    public DataSource(Context context) {
        rssFeedTable = new RssFeedTable();
        rssItemTable = new RssItemTable();

        executorService = Executors.newSingleThreadExecutor();

        databaseOpenHelper = new DatabaseOpenHelper(context,
                rssFeedTable, rssItemTable);

        if (BuildConfig.DEBUG && true) {
            context.deleteDatabase("blocly_db");
            SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();
            new RssFeedTable.Builder()
                    .setTitle("AndroidCentral")
                    .setDescription("AndroidCentral - Android News, Tips, and stuff!")
                    .setSiteURL("http://www.androidcentral.com")
                    .setFeedURL("http://feeds.feedburner.com/androidcentral?format=xml")
                    .insert(writableDatabase);
            new RssFeedTable.Builder()
                    .setTitle("IGN")
                    .setDescription("IGN All")
                    .setSiteURL("http://www.ign.com")
                    .setFeedURL("http://feeds.ign.com/ign/all?format=xml")
                    .insert(writableDatabase);
            new RssFeedTable.Builder()
                    .setTitle("Kotaku")
                    .setDescription("Game news, reviews, and awesomeness")
                    .setSiteURL("http://kotaku.com")
                    .setFeedURL("http://feeds.gawker.com/kotaku/full#_ga=1.41426146.1734638996.1420673722")
                    .insert(writableDatabase);
        }
    }

    public void fetchRSSItemWithId(final long rowId, final Callback<RssItem> callback) {
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = rssItemTable.fetchRow(databaseOpenHelper.getReadableDatabase(), rowId);
                if (cursor.moveToFirst()) {
                    final RssItem rssItem = itemFromCursor(cursor);
                    cursor.close();
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(rssItem);
                        }
                    });
                } else {
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("RSS item not found for row Id (" + rowId + ")");
                        }
                    });
                }
            }
        });
    }

    public void fetchFeedWithId(final long rowId, final Callback<RssFeed> callback) {
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = rssFeedTable.fetchRow(databaseOpenHelper.getReadableDatabase(), rowId);
                if (cursor.moveToFirst()) {
                    final RssFeed rssFeed = feedFromCursor(cursor);
                    cursor.close();
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(rssFeed);
                        }
                    });
                } else {
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("RSS feed not found for row Id (" + rowId + ")");
                        }
                    });
                }
            }
        });
    }

    public void fetchAllFeeds(final Callback<List<RssFeed>> callback) {
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                final List<RssFeed> resultFeeds = new ArrayList<RssFeed>();
                Cursor cursor = RssFeedTable.fetchAllFeeds(databaseOpenHelper.getReadableDatabase());
                if (cursor.moveToFirst()) {
                    do {
                        resultFeeds.add(feedFromCursor(cursor));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(resultFeeds);
                    }
                });
            }
        });
    }

    public void fetchNewItemsForFeed(final RssFeed rssFeed, final Callback<List<RssItem>> callback) {
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                GetFeedsNetworkRequest getFeedsNetworkRequest = new GetFeedsNetworkRequest(rssFeed.getFeedUrl());
                final List<RssItem> newItems = new ArrayList<RssItem>();
                List<GetFeedsNetworkRequest.FeedResponse> feedResponses = getFeedsNetworkRequest.performRequest();
                if (checkForError(getFeedsNetworkRequest, callbackThreadHandler, callback)) {
                    return;
                }
                GetFeedsNetworkRequest.FeedResponse feedResponse = feedResponses.get(0);
                for (GetFeedsNetworkRequest.ItemResponse itemResponse : feedResponse.channelItems) {
                    if (RssItemTable.hasItem(databaseOpenHelper.getReadableDatabase(), itemResponse.itemGUID)) {
                        continue;
                    }
                    long newItemRowId = insertResponseToDatabase(rssFeed.getRowId(), itemResponse);
                    Cursor newItemCursor = rssItemTable.fetchRow(databaseOpenHelper.getReadableDatabase(), newItemRowId);
                    newItemCursor.moveToFirst();
                    newItems.add(itemFromCursor(newItemCursor));
                    newItemCursor.close();
                }
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(newItems);
                    }
                });
            }
        });
    }

    public void fetchNewFeed(final String feedURL, final Callback<RssFeed> callback) {
        final Handler callbackThreadHandler = new Handler();

        submitTask(new Runnable() {
            @Override
            public void run(){
                Cursor existingFeedCursor = RssFeedTable.fetchFeedWithURL(databaseOpenHelper.getReadableDatabase(), feedURL);
                if(existingFeedCursor.moveToFirst())
                {
                    final RssFeed fetchedFeed = feedFromCursor(existingFeedCursor);
                    existingFeedCursor.close();
                    callbackThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(fetchedFeed);
                        }
                    });
                    return;
                }

                GetFeedsNetworkRequest getFeedsNetworkRequest = new GetFeedsNetworkRequest(feedURL);
                List<GetFeedsNetworkRequest.FeedResponse> feedResponses = getFeedsNetworkRequest.performRequest();
                if (checkForError(getFeedsNetworkRequest, callbackThreadHandler, callback)) {
                    return;
                }

                GetFeedsNetworkRequest.FeedResponse newFeedResponse = feedResponses.get(0);
                long newFeedId = new RssFeedTable.Builder()
                        .setFeedURL(newFeedResponse.channelFeedURL)
                        .setSiteURL(newFeedResponse.channelURL)
                        .setTitle(newFeedResponse.channelTitle)
                        .setDescription(newFeedResponse.channelDescription)
                        .insert(databaseOpenHelper.getWritableDatabase());

                for(GetFeedsNetworkRequest.ItemResponse itemResponse : newFeedResponse.channelItems)
                {
                    insertResponseToDatabase(newFeedId, itemResponse);
                }

                Cursor newFeedCursor = rssFeedTable.fetchRow(databaseOpenHelper.getReadableDatabase(), newFeedId);
                newFeedCursor.moveToFirst();
                final RssFeed fetchedFeed = feedFromCursor(newFeedCursor);
                newFeedCursor.close();
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(fetchedFeed);
                    }
                });
            }
        });
    }
    public void fetchItemsForFeed(final RssFeed rssFeed, final Callback<List<RssItem>> callback) {
        final Handler callbackThreadHandler = new Handler();
        submitTask(new Runnable() {
            @Override
            public void run() {
                final List<RssItem> resultList = new ArrayList<RssItem>();
                Cursor cursor = RssItemTable.fetchItemsForFeed(
                        databaseOpenHelper.getReadableDatabase(),
                        rssFeed.getRowId());
                if (cursor.moveToFirst()) {
                    do {
                        resultList.add(itemFromCursor(cursor));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                callbackThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(resultList);
                    }
                });
            }
        });
    }

    static RssFeed feedFromCursor(Cursor cursor) {
        return new RssFeed(Table.getRowId(cursor), RssFeedTable.getTitle(cursor),
                RssFeedTable.getDescription(cursor), RssFeedTable.getSiteURL(cursor),
                RssFeedTable.getFeedURL(cursor));
    }

    static RssItem itemFromCursor(Cursor cursor) {
        return new RssItem(Table.getRowId(cursor), RssItemTable.getGUID(cursor), RssItemTable.getTitle(cursor),
                RssItemTable.getDescription(cursor), RssItemTable.getLink(cursor),
                RssItemTable.getEnclosure(cursor), RssItemTable.getRssFeedId(cursor),
                RssItemTable.getPubDate(cursor), RssItemTable.getFavorite(cursor),
                RssItemTable.getArchived(cursor));
    }
    void submitTask(Runnable task) {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(task);
    }

    boolean checkForError(GetFeedsNetworkRequest getFeedsNetworkRequest, Handler callbackThreadHandler, final Callback<?> callback) {
        if (getFeedsNetworkRequest.getErrorCode() != 0) {
            final String errorMessage;
            if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_IO) {
                errorMessage = "Network error";
            } else if (getFeedsNetworkRequest.getErrorCode() == NetworkRequest.ERROR_MALFORMED_URL) {
                errorMessage = "Malformed URL error";
            } else if (getFeedsNetworkRequest.getErrorCode() == GetFeedsNetworkRequest.ERROR_PARSING) {
                errorMessage = "Error parsing feed";
            } else {
                errorMessage = "Error unknown";
            }
            callbackThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(errorMessage);
                }
            });
        }
        return getFeedsNetworkRequest.getErrorCode() != 0;
    }

    long insertResponseToDatabase(long feedId, GetFeedsNetworkRequest.ItemResponse itemResponse) {
        long itemPubDate = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
        try {
            itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new RssItemTable.Builder()
                .setTitle(itemResponse.itemTitle)
                .setDescription(itemResponse.itemDescription)
                .setEnclosure(itemResponse.itemEnclosureURL)
                .setMIMEType(itemResponse.itemEnclosureMIMEType)
                .setLink(itemResponse.itemURL)
                .setGUID(itemResponse.itemGUID)
                .setPubDate(itemPubDate)
                .setRSSFeed(feedId)
                .insert(databaseOpenHelper.getWritableDatabase());
    }


    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_GUID = "guid";
    private static final String COLUMN_PUB_DATE = "pub_date";
    private static final String COLUMN_ENCLOSURE = "enclosure";
    private static final String COLUMN_MIME_TYPE = "mime_type";
    private static final String COLUMN_RSS_FEED = "rss_feed";
    private static final String COLUMN_FAVORITE = "is_favorite";
    private static final String COLUMN_ARCHIVED = "is_archived";

    public void updateItemInTable(String link, String title, String description, String guID,
                                      long pubDate, String enclosure, String mimeType, long rssFeedId,
                                      boolean isFavorite, boolean isArchived) {
        //rssItemTable.updateFavorite(guID, databaseOpenHelper.getWritableDatabase(), isFavorite);
        ContentValues cv = new ContentValues();
        if(link != null)
            cv.put("link", link);
        if(title != null)
            cv.put("title", title);
        if(description != null)
            cv.put("description", description);
        if(guID != null)
            cv.put("guid", guID);
        if(pubDate != -1)
            cv.put("pub_date", pubDate);
        if(enclosure != null)
            cv.put("enclosure", enclosure);
        if(mimeType != null)
            cv.put("mime_type", mimeType);
        if(rssFeedId != -1)
            cv.put("rss_feed", rssFeedId);
        if(isFavorite)
            cv.put("is_favorite",1);
        else
            cv.put("is_favorite",0);
        if(isArchived)
            cv.put("is_archived",1);
        else
            cv.put("is_archived",0);
        databaseOpenHelper.getWritableDatabase().update(rssItemTable.getName(), cv,
                rssItemTable.getTableColumnID() + " = ?", new String[]{guID});
    }
}