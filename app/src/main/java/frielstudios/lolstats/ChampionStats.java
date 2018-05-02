package frielstudios.lolstats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Cameron on 5/1/2018.
 */

public class ChampionStats extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ChampionStats.ChampionStatsData> {
    private final static int SUMMONER_LOADER_ID = 0; //id to load user account

    private RecyclerView statsView;

    private ChampionStatsAdapter statsAdapter;

    private String search; //string that stores the user's search string

    private ProgressBar loadPending;
    private TextView loadError;

    private ImageView championImage;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_stats);

        championImage = (ImageView) findViewById(R.id.champImage);

        statsView = (RecyclerView) findViewById(R.id.statsView);
        statsView.setLayoutManager(new LinearLayoutManager(this));
        statsView.setHasFixedSize(true);

        statsAdapter = new ChampionStatsAdapter();
        statsView.setAdapter(statsAdapter);

        loadPending = (ProgressBar) findViewById(R.id.loadPending);
        loadError = (TextView) findViewById(R.id.loadError);

        //loadPending.setVisibility(View.VISIBLE); //display to user that their data is being fetched

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SummonerStats.NAME_KEY) && intent.hasExtra(SummonerStats.MATCH_LIST_KEY)) {
            //getSupportLoaderManager().initLoader(SUMMONER_LOADER_ID, null, this);
            String name = intent.getStringExtra(SummonerStats.NAME_KEY);
            System.out.println("Ohh: " + name);
            Glide.with(this).load("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/"+name+"_0.jpg").into(championImage);
            String matchList = intent.getStringExtra(SummonerStats.MATCH_LIST_KEY);
        }
    }

    private void executeSearch(String search) {
        String searchURL = DataUtils.buildSearchURL(search);

        Bundle args = new Bundle(); //create a new bundle to be used by loader with our data and key
        args.putString("searchURL", searchURL);
        getSupportLoaderManager().restartLoader(SUMMONER_LOADER_ID, args, this); //load in content given by user arguments
    }

    @Override
    public Loader<ChampionStats.ChampionStatsData> onCreateLoader(int i, Bundle bundle) {
        String searchURL = null;

        if (bundle != null) {
            searchURL = bundle.getString("searchURL");
        }
        return new ChampionStats.ChampionSearchLoader(this, searchURL);
    }

    @Override
    public void onLoadFinished(Loader<ChampionStats.ChampionStatsData> loader, ChampionStats.ChampionStatsData data) {
        loadPending.setVisibility(View.INVISIBLE);
        Log.d(TAG, "loader finished loading");
        if (data != null) {
            statsAdapter.updateSearchResults();

            loadError.setVisibility(View.INVISIBLE);
            statsView.setVisibility(View.VISIBLE);
        } else {
            statsView.setVisibility(View.INVISIBLE);
            loadError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ChampionStats.ChampionStatsData> loader) {

    }

    public static class ChampionStatsData {
        ArrayList<String> championImages; //holds images URLs
        ArrayList<String> championNames; //holds names of images
        ArrayList<Integer> championWinRates; //holds percentage of wins for given champion
        ArrayList<Integer> championPerformances; //holds whether the user is doing well on the champion or not
        ArrayList<String> matchJSON; //holds JSON data for specific match information
    }

    static class ChampionSearchLoader extends AsyncTaskLoader<ChampionStats.ChampionStatsData> {

        String searchURL; //URL for a user's information
        ChampionStats.ChampionStatsData searchData;

        ChampionSearchLoader(Context context, String searchURL) {
            super(context);
            this.searchURL = searchURL;
        }

        @Override
        protected void onStartLoading() {
            if (searchURL != null) {
                if (searchData != null) {
                }
                System.out.println("IN");
                deliverResult(searchData);
            } else {
                forceLoad();
            }
        }

        @Override
        public ChampionStats.ChampionStatsData loadInBackground() { //NEED TO PASS AN OBJECT THAT HOLDS CHAMP IDS AND THE JSON TO THE MATCHES TO GET WIN RATE
            if (searchURL != null) {
                ChampionStats.ChampionStatsData searchResults = new ChampionStats.ChampionStatsData();

                try {
                    String userResults = NetworkUtils.doHTTPGet(searchURL);
                    Log.d(TAG, "MY ACCOUNT URL: " + searchURL);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return searchResults;
            } else {
                return null;
            }
        }

        public void deliverResult(ChampionStats.ChampionStatsData data) {
            searchData = data;
            super.deliverResult(data);
        }
    }
}
