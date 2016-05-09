package com.jamiefarrelly.luasstriketracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fabUp)
    FloatingActionButton fabUp;
    @Bind(R.id.fabDown)
    FloatingActionButton fabDown;
    @Bind(R.id.tvSmileOrSad)
    TextView tvSmileOrSad;
    @Bind(R.id.tvOnStrike)
    TextView tvOnStrike;
    @Bind(R.id.tvNextStrikeDate)
    TextView tvNextStrikeDate;
    @Bind(R.id.tvErrorMessage)
    TextView tvErrorMessage;

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        String apiResponse = "";
        String nextStrike = "";
        try {
            apiResponse = new HttpUtils().execute().get();

            JSONObject mainObject = new JSONObject(apiResponse);
            nextStrike = mainObject.getString("nextStrike");
        } catch (Exception e) {
            Log.d(Constants.LOG, e.getMessage());
        }

        // this is the format that the date will be returned in from the API call
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String todaysDate = fmt.format(calendar.getTime());

        try {
            Date dateToday = fmt.parse(todaysDate);
            Date dateNextStrike = fmt.parse(nextStrike);

            calendar.setTime(dateToday);
            calendar.add(Calendar.DATE, 1);
            Date dateTomorrow = calendar.getTime();

            if (dateToday.equals(dateNextStrike)) {
                tvOnStrike.setText(getString(R.string.on_strike));
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));
                tvSmileOrSad.setText(":(");
            } else if (dateTomorrow.equals(dateNextStrike)) {
                tvOnStrike.setText(getString(R.string.on_strike_tomorrow));
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));
                tvSmileOrSad.setText(":(");
            } else {
                tvOnStrike.setText(getString(R.string.not_on_strike));
            }

            // the API call will return a date in the past if there's no Luas strikes planned
            if (dateNextStrike.before(dateToday)) {
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), getString(R.string.no_strike_planned)));
            } else if (!dateToday.equals(dateNextStrike)){
                // only show the next strike date if there's no strike today, makes no sense to show
                // that there's a strike today and also have it as the next strike date
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), nextStrike));
            }

        } catch (ParseException e) {
            Log.d(Constants.LOG, "Couldn't parse a date: " + e.getMessage());
        }

        if (apiResponse == null || apiResponse.equals("ERROR")) {
            tvSmileOrSad.setText(":(");
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }
}
