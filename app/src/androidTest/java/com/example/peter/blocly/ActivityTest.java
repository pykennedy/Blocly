package com.example.peter.blocly;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;

import com.example.peter.blocly.ui.activity.BloclyActivity;
import com.example.peter.blocly.ui.fragment.RssItemListFragment;

/**
 * Created by Peter on 12/5/2015.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<BloclyActivity> {

    public ActivityTest() {
        super(BloclyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testStarExists() throws Exception {
        BloclyActivity activity = getActivity();
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);
        RecyclerView recyclerView = listFragment.getRecyclerView();
        while(recyclerView.getAdapter().getItemCount() == 0) {
                Thread.sleep(1000);
        }
        assertNotNull(recyclerView.findViewById(R.id.cb_rss_item_favorite_star));
    }

    public void testStarWorks() throws Exception {
        setActivityInitialTouchMode(true);
        BloclyActivity activity = getActivity();
        FragmentManager fragMan = activity.getSupportFragmentManager();
        RssItemListFragment listFragment =
                (RssItemListFragment) fragMan.findFragmentById(R.id.fl_activity_blocly);
        RecyclerView recyclerView = listFragment.getRecyclerView();
        while(recyclerView.getAdapter().getItemCount() == 0) {
            Thread.sleep(1000);
        }
        onView(withId(R.id.cb_rss_item_favorite_star)).perform(click());
        //assertNotNull(recyclerView.findViewById(R.id.cb_rss_item_favorite_star));
    }
}
