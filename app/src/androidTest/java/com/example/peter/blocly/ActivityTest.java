package com.example.peter.blocly;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.SingleLaunchActivityTestCase;
import android.view.View;

import com.example.peter.blocly.api.model.RssItem;
import com.example.peter.blocly.ui.activity.BloclyActivity;
import com.example.peter.blocly.ui.adapter.ItemAdapter;
import com.example.peter.blocly.ui.fragment.RssItemListFragment;

/**
 * Created by Peter on 12/5/2015.
 */
public class ActivityTest extends SingleLaunchActivityTestCase<BloclyActivity> {

    public ActivityTest() {
        super("com.example.peter.blocly", BloclyActivity.class);
    }

    private BloclyActivity activity;
    private boolean starShouldBeChecked;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        starShouldBeChecked = false;
        activity = getActivity();
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);

        RecyclerView recyclerView = listFragment.getRecyclerView();
        while(recyclerView.getAdapter().getItemCount() == 0) {
            Thread.sleep(1000);
        }
    }
    public void testStarExists() throws Exception {
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);
        RecyclerView recyclerView = listFragment.getRecyclerView();
        assertNotNull(recyclerView.findViewById(R.id.cb_rss_item_favorite_star));

        // throws error, keep for testing
        //assertNotNull(recyclerView.findViewById(R.id.dl_activity_blocly));
    }

    public void testStarWorks() throws Exception {
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);
        RecyclerView recyclerView = listFragment.getRecyclerView();

        final View favorite = recyclerView.findViewById(R.id.cb_rss_item_favorite_star);

        favorite.post(new Runnable() {
            @Override
            public void run() {
                starShouldBeChecked = !starShouldBeChecked;
                favorite.performClick();
            }
        });
        ItemAdapter.ItemAdapterViewHolder viewHolder = (ItemAdapter.ItemAdapterViewHolder) favorite.getTag();
        RssItem item = viewHolder.getRssItem();

        //Tests
        assertEquals(starShouldBeChecked, item.isFavorite());

        //Database code
        RssItem retrievedItem = ((BloclyApplication)getActivity().getApplication())
                .getDataSource()
                .fetchRssItemSync(item.getRowId());

        assertNotNull(retrievedItem);
        assertTrue(starShouldBeChecked == retrievedItem.isFavorite());

        /*
        //Datasource code
    public RssItem fetchRssItemSync(long rowId){
        Cursor cursor = rssItemTable.fetchRow(databaseOpenHelper.getReadableDatabase(), rowId);
        RssItem item = null;
        if (cursor.moveToFirst()) {
            item = itemFromCursor(cursor);
        }
        cursor.close();
        return item;
    }
         */

    }
}