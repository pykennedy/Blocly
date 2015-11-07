package com.example.peter.blocly.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.blocly.BloclyApplication;
import com.example.peter.blocly.R;

public class BloclyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);
        TextView hello_world = (TextView)findViewById(R.id.hello_world);
        hello_world.setText(BloclyApplication.getSharedDataSource().getFeeds()
                            .get(0).getTitle());

        Toast.makeText(this,
                BloclyApplication.getSharedDataSource().getFeeds().get(0).getTitle(),
                Toast.LENGTH_LONG).show();
    }
}
