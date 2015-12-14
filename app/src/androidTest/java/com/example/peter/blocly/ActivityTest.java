package com.example.peter.blocly;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.SingleLaunchActivityTestCase;
import android.view.View;

import com.example.peter.blocly.api.DataSource;
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
        if(starShouldBeChecked)
            assertEquals(true, item.isFavorite());
        else
            assertEquals(false, item.isFavorite());

        //Database code

        DataSource source = ((BloclyApplication) getActivity().getApplication()).getDataSource();
        source.fetchRSSItemWithId(item.getRowId(), new DataSource.Callback<RssItem>() {
            @Override
            public void onSuccess(RssItem rssItem) {
                assertTrue(rssItem.isFavorite());
            }

            @Override
            public void onError(String errorMessage) {
                //pfff
                fail();
            }
        });
    }
}