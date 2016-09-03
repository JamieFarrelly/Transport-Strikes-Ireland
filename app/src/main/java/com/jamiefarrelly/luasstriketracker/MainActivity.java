package com.jamiefarrelly.luasstriketracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fabUp)
    FloatingActionButton fabUp;
    @BindView(R.id.fabDown)
    FloatingActionButton fabDown;
    @BindView(R.id.tvSmileOrSad)
    FontAwesomeText tvSmileOrSad;
    @BindView(R.id.tvOnStrike)
    TextView tvOnStrike;
    @BindView(R.id.tvNextStrikeDate)
    TextView tvNextStrikeDate;
    @BindView(R.id.tvErrorMessage)
    TextView tvErrorMessage;
    @BindView(R.id.publisherAdView)
    PublisherAdView mPublisherAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.fab_up, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fabDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.fab_down, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();

        String apiResponse = "";
        String nextStrikeFormatted = "";
        // sometimes the strikes are only for a few hours, eg. 3pm - 7pm
        String strikeHours = "";
        try {
            apiResponse = new HttpUtils().execute().get();

            JSONObject strikeInformation = new JSONObject(apiResponse);
            nextStrikeFormatted = strikeInformation.getString("nextStrike");
            strikeHours = strikeInformation.getString("strikeHours");
        } catch (Exception e) {
            Log.d(Constants.LOG, e.getMessage());
        }

        // this is the format that the date will be returned in from the API call
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String todayFormatted = fmt.format(calendar.getTime());

        try {
            Date today = fmt.parse(todayFormatted);
            Date nextStrike = fmt.parse(nextStrikeFormatted);

            calendar.setTime(today);
            calendar.add(Calendar.DATE, 1);
            Date tomorrow = calendar.getTime();

            if (today.equals(nextStrike)) {
                tvOnStrike.setText(String.format(getString(R.string.on_strike), strikeHours));
                tvSmileOrSad.setIcon("fa-frown-o");
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));
                tvSmileOrSad.setTextColor(this.getResources().getColor(R.color.red));

            } else if (tomorrow.equals(nextStrike)) {
                tvOnStrike.setText(getString(R.string.on_strike_tomorrow));
                tvSmileOrSad.setIcon("fa-frown-o");
                tvSmileOrSad.setTextColor(this.getResources().getColor(R.color.red));
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));

            } else {
                tvOnStrike.setText(getString(R.string.not_on_strike));

            }

            // the API call will return a date in the past if there's no Luas strikes planned
            if (nextStrike.before(today)) {
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), getString(R.string.no_strike_planned)));
            } else if (!today.equals(nextStrike)){
                // only show the next strike date if there's no strike today, makes no sense to show
                // that there's a strike today and also have it as the next strike date
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), nextStrikeFormatted));
            }

        } catch (ParseException e) {
            Log.d(Constants.LOG, "Couldn't parse a date: " + e.getMessage());
        }

        if (apiResponse == null || apiResponse.equals("ERROR")) {
            tvSmileOrSad.setIcon("fa-frown-o");
            tvSmileOrSad.setTextColor(this.getResources().getColor(R.color.red));
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }
}
