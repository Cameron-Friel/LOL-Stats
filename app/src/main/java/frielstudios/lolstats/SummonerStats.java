package frielstudios.lolstats;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import static android.content.ContentValues.TAG;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cameron on 2/15/2018.
 */

public class SummonerStats extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private final static int SUMMONER_LOADER_ID = 0; //id to load user account

    private RecyclerView statsView;

    private SummonerStatsAdapter statsAdapter;

    private String search; //string that stores the user's search string

    private TextView userName;
    private TextView summonerLevel;

    private ProgressBar loadPending;
    private TextView loadError;

    private ImageView championImage;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //userName = (TextView)findViewById(R.id.userName);
        //summonerLevel = (TextView)findViewById(R.id.summonerLevel);
        //championImage = (ImageView)findViewById(R.id.champImage);

        statsView = (RecyclerView)findViewById(R.id.statsView);
        statsView.setLayoutManager(new LinearLayoutManager(this));
        statsView.setHasFixedSize(true);

        statsAdapter = new SummonerStatsAdapter();
        statsView.setAdapter(statsAdapter);

        loadPending = (ProgressBar)findViewById(R.id.loadPending);
        loadError = (TextView)findViewById(R.id.loadError);

        //Picasso.with(this).load("https://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/Annie.png").resize(500, 500).into(championImage);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("test")) {
            getSupportLoaderManager().initLoader(SUMMONER_LOADER_ID, null, this);
            search = intent.getStringExtra("test");
            executeSearch(search);
        }
    }

    private void executeSearch(String search) {

        String searchURL = DataUtils.buildSearchURL(search);
        String matchListURL = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/";
        String matchURL = "https://na1.api.riotgames.com/lol/match/v3/matches/";

        Bundle args = new Bundle(); //create a new bundle to be used by loader with our data and key
        args.putString("searchURL", searchURL);
        args.putString("test", searchURL);
        getSupportLoaderManager().restartLoader(SUMMONER_LOADER_ID, args,this); //load in content given by user arguments
        getSupportLoaderManager().restartLoader(1, args, this);
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        String searchURL = null;

        if (bundle != null) {
            if (i == 0) {
                searchURL = bundle.getString("searchURL");
            }
            else {
                searchURL = bundle.getString("test");
            }
        }
        Log.d(TAG, "MY URL: " + searchURL);
        return new SearchLoader(this, searchURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        loadPending.setVisibility(View.INVISIBLE);
        //Log.d(TAG, "loader finished loading");
        if (s != null) {
            ArrayList<DataUtils.SearchResult> searchResults = DataUtils.parseJSON(s); //parse the username, level, and accountid
            Log.d(TAG, "loader returning cached results" + searchResults);
            //userName.setText(searchResults.get(0).userName);
            //summonerLevel.setText(searchResults.get(0).summonerLevel);

            statsAdapter.updateSearchResults(searchResults, "https://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/Annie.png");
            loadError.setVisibility(View.INVISIBLE);
            statsView.setVisibility(View.VISIBLE);
        } else {
            statsView.setVisibility(View.INVISIBLE);
            loadError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    static class SearchLoader extends AsyncTaskLoader<String> {

        String searchURL; //URL for a user's information
        String searchData;

        SearchLoader(Context context, String searchURL) {
            super(context);
            this.searchURL = searchURL;
        }

        @Override
        protected void onStartLoading() {
            if (searchURL != null) {
                if (searchData != null) {
                    deliverResult(searchData);
                }
                else {
                    forceLoad();
                }
            }
        }

        @Override
        public String loadInBackground() {
            if (searchURL != null) {
                String searchResults = null;
                try {
                    searchResults = NetworkUtils.doHTTPGet(searchURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return searchResults;
            } else {
                return null;
            }
        }

        public void deliverResult(String data) {
            searchData = data;
            super.deliverResult(data);
        }
    }
}
