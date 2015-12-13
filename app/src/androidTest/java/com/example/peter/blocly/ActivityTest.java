package com.example.peter.blocly;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.SingleLaunchActivityTestCase;

import com.example.peter.blocly.ui.activity.BloclyActivity;
import com.example.peter.blocly.ui.fragment.RssItemListFragment;

/**
 * Created by Peter on 12/5/2015.
 */
public class ActivityTest extends SingleLaunchActivityTestCase<BloclyActivity> {

    public ActivityTest() {
        super("com.example.peter.blocly", BloclyActivity.class);
    }

    private BloclyActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();


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
/*
    public void testStarWorks() throws Exception {
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);
        RecyclerView recyclerView = listFragment.getRecyclerView();
        //onView(withId(R.id.cb_rss_item_favorite_star)).perform(click());
        //assertNotNull(recyclerView.findViewById(R.id.cb_rss_item_favorite_star));
        RssItem theItem = listFragment.getRssItem(listFragment.getItemAdapter(), 1);
        System.out.println(theItem.getTitle());
        //assertEquals(true, theItem.isFavorite());
        // just throwing this in here to have something for this test to run
        //assertNotNull(recyclerView.findViewById(R.id.cb_rss_item_favorite_star));
    } */
}