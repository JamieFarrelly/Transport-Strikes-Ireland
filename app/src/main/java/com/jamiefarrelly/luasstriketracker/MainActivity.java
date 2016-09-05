package com.jamiefarrelly.luasstriketracker;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.jamiefarrelly.luasstriketracker.interfaces.StrikeInfoAPIInterface;
import com.jamiefarrelly.luasstriketracker.model.StrikeInfoModel;
import com.jamiefarrelly.luasstriketracker.network.ServiceGenerator;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

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
    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;

    private StrikeInfoAPIInterface strikeInfoService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        strikeInfoService = ServiceGenerator.createService(StrikeInfoAPIInterface.class);

        setupBottomBar();

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveStrikeInfo();
    }

    private void retrieveStrikeInfo(){

        Call<StrikeInfoModel> strikeInfo = strikeInfoService.getStrikeInfo();

        strikeInfo.enqueue(new Callback<StrikeInfoModel>() {
            @Override
            public void onResponse(Call<StrikeInfoModel> call, Response<StrikeInfoModel> response) {

                if(response.isSuccessful()){
                    processResults(response.body());
                }
                else{
                    tvSmileOrSad.setIcon("fa-frown-o");
                    tvSmileOrSad.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                    tvErrorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<StrikeInfoModel> call, Throwable t) {
                tvSmileOrSad.setIcon("fa-frown-o");
                tvSmileOrSad.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    public void processResults(StrikeInfoModel model){

        if (model!=null) {

            String nextStrikeDate = "";
            String nextStrikeHours = "";

            if(mBottomBar.getCurrentTabPosition()==0){
                nextStrikeDate = model.getNextLuasStrikeDate();
                nextStrikeHours = model.getNextLuasStrikeHours();
                System.out.println(nextStrikeDate + "   "+nextStrikeHours);
            }
            else if(mBottomBar.getCurrentTabPosition()==1){
                nextStrikeDate = model.getNextDublinBusStrikeDate();
                nextStrikeHours = model.getNextDublinBusStrikeHours();
                System.out.println(nextStrikeDate + "   "+nextStrikeHours);

            }

            // this is the format that the date will be returned in from the API call
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy",Locale.UK);
            Calendar calendar = Calendar.getInstance();
            String todayFormatted = fmt.format(calendar.getTime());

            try {
                Date today = fmt.parse(todayFormatted);
                Date nextStrike = fmt.parse(nextStrikeDate);

                calendar.setTime(today);
                calendar.add(Calendar.DATE, 1);
                Date tomorrow = calendar.getTime();

                if (today.equals(nextStrike)) {
                    tvOnStrike.setText(String.format(getString(R.string.on_strike), nextStrikeHours));
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
                    tvNextStrikeDate.setText(String.format(getString(R.string.next_strike), fmt.format(nextStrike)));
                }

            } catch (ParseException e) {
                Log.d(Constants.LOG, "Couldn't parse a date: " + e.getMessage());
            }
        } else {
            Toast.makeText(MainActivity.this, "Error retrieving strike info", Toast.LENGTH_SHORT).show();
        }

    }

    public void setupBottomBar(){

        mBottomBar.setOnTabSelectListener(tabId -> {
            if (tabId == R.id.tab_luas) {
                retrieveStrikeInfo();
            }
            else if(tabId == R.id.tab_dublin_bus){
                retrieveStrikeInfo();
            }
        });
    }

    @OnClick(R.id.fabUp)
    public void fabUpAction(View view){
        Snackbar.make(view, R.string.fab_up, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @OnClick(R.id.fabDown)
    public void fabDownAction(View view){
        Snackbar.make(view, R.string.fab_down, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
