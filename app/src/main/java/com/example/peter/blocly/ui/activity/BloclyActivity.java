package com.example.peter.blocly.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.peter.blocly.R;
import com.example.peter.blocly.api.model.RssFeed;
import com.example.peter.blocly.ui.adapter.ItemAdapter;
import com.example.peter.blocly.ui.adapter.NavigationDrawerAdapter;

public class BloclyActivity extends ActionBarActivity
                            implements NavigationDrawerAdapter.NavigationDrawerAdapterDelegate,
                            ItemAdapter.ItemAdapterDelegate {

    private ItemAdapter itemAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationDrawerAdapter navigationDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_activity_blocly);
        setSupportActionBar(toolbar);

        itemAdapter = new ItemAdapter();
        itemAdapter.setDelegate(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_activity_blocly);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_blocly);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.openDrawer(Gravity.LEFT);

        navigationDrawerAdapter = new NavigationDrawerAdapter();
        navigationDrawerAdapter.setDelegate(this);
        RecyclerView navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_blocly);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(navigationDrawerAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void didSelectNavigationOption(NavigationDrawerAdapter adapter, NavigationDrawerAdapter.NavigationOption navigationOption) {
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show the " + navigationOption.name(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didSelectFeed(NavigationDrawerAdapter adapter, RssFeed rssFeed) {
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show RSS items from " + rssFeed.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastExpand(ItemAdapter adapter) {
        Toast.makeText(this, "Expanded item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastContract(ItemAdapter adapter) {
        Toast.makeText(this, "Contracted item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastVisit(ItemAdapter adapter, View view, String url) {
        Toast.makeText(view.getContext(), "Visit " + url, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastFavUnFav(ItemAdapter adapter, boolean favorited) {
        if(favorited)
            Toast.makeText(this, "Favorited this item", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "UnFavorited this item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastArchive(ItemAdapter adapter) {
        Toast.makeText(this, "Archived this item", Toast.LENGTH_SHORT).show();
    }
}
