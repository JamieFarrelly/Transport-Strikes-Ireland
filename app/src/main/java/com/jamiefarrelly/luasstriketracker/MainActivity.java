package com.jamiefarrelly.luasstriketracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabUp = (FloatingActionButton) findViewById(R.id.fabUp);
        FloatingActionButton fabDown = (FloatingActionButton) findViewById(R.id.fabDown);
        ImageView ivSmileOrSad = (ImageView) findViewById(R.id.ivSmileOrSad);
        TextView tvOnStrike = (TextView) findViewById(R.id.tvOnStrike);
        TextView tvNextStrikeDate = (TextView) findViewById(R.id.tvNextStrikeDate);
        TextView tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);


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
        todaysDate = "30-05-2016"; //testing

        try {
            Date dateToday = fmt.parse(todaysDate);
            Date dateNextStrike = fmt.parse(nextStrike);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date dateTomorrow = calendar.getTime();

            if (dateToday.equals(dateNextStrike)) {
                tvOnStrike.setText(getString(R.string.on_strike));
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));
                ivSmileOrSad.setImageResource(R.drawable.sad);
            } else if (dateTomorrow.equals(dateNextStrike)) {
                tvOnStrike.setText(getString(R.string.on_strike_tomorrow));
                tvOnStrike.setTextColor(this.getResources().getColor(R.color.red));
                ivSmileOrSad.setImageResource(R.drawable.sad);
            } else {
                tvOnStrike.setText(getString(R.string.not_on_strike));
            }

            // the API call will return a date in the past if there's no Luas strikes planned
            if (dateNextStrike.before(dateToday)) {
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), R.string.no_strike_planned));
            } else {
                tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), nextStrike));
            }

        } catch (ParseException e) {
            Log.d(Constants.LOG, "Couldn't parse a date: " + e.getMessage());
        }

        if (apiResponse == null || apiResponse.equals("ERROR")) {
            ivSmileOrSad.setImageResource(R.drawable.sad);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
