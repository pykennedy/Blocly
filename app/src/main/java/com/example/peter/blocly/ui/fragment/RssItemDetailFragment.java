package com.example.peter.blocly.ui.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.peter.blocly.BloclyApplication;
import com.example.peter.blocly.R;
import com.example.peter.blocly.api.DataSource;
import com.example.peter.blocly.api.model.RssItem;
import com.example.peter.blocly.ui.activity.BloclyActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class RssItemDetailFragment extends Fragment implements ImageLoadingListener {

    private static final String BUNDLE_EXTRA_RSS_ITEM = RssItemDetailFragment.class.getCanonicalName().concat(".EXTRA_RSS_ITEM");

    private Menu menu;

    static boolean isFirstTimeLoading = true;

    public static RssItemDetailFragment detailFragmentForRssItem(RssItem rssItem) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_EXTRA_RSS_ITEM, rssItem.getRowId());
        RssItemDetailFragment rssItemDetailFragment = new RssItemDetailFragment();
        rssItemDetailFragment.setArguments(arguments);
        return rssItemDetailFragment;
    }

    ImageView headerImage;
    TextView title;
    TextView content;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("GOT HERE AT THE START");
        super.onCreate(savedInstanceState);


        if(isFirstTimeLoading) {
            isFirstTimeLoading = false;
            BloclyActivity activity = (BloclyActivity) this.getActivity();
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.tb_item_detail_box);
            toolbar.inflateMenu(R.menu.item_detail_menu);

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.item_menu_visit_page) {
                        System.out.println("BIG BUTS CAN'T LIE");
                    }
                    else
                        System.out.println("god dam");
                }
            });
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            long rssItemId = arguments.getLong(BUNDLE_EXTRA_RSS_ITEM);
            BloclyApplication.getSharedDataSource().fetchRSSItemWithId(rssItemId, new DataSource.Callback<RssItem>() {
                @Override
                public void onSuccess(RssItem rssItem) {
                    if (getActivity() == null) {
                        return;
                    }
                    title.setText(rssItem.getTitle());
                    content.setText(rssItem.getDescription());
                    ImageLoader.getInstance().loadImage(rssItem.getImageUrl(), RssItemDetailFragment.this);
                }

                @Override
                public void onError(String errorMessage) {}
            });
        }

        setHasOptionsMenu(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("got to OnOptionsItemSelects");
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_rss_item_detail, container, false);
        headerImage = (ImageView) inflate.findViewById(R.id.iv_fragment_rss_item_detail_header);
        progressBar = (ProgressBar) inflate.findViewById(R.id.pb_fragment_rss_item_detail_header);
        title = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_title);
        content = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_content);
        setHasOptionsMenu(true);
        return inflate;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        progressBar.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.setImageBitmap(loadedImage);
        headerImage.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {}
}