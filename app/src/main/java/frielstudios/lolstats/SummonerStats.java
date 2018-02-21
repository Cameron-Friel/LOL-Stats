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

public class SummonerStats extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SummonerStats.SummonerStatsData> {

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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("test")) {
            getSupportLoaderManager().initLoader(SUMMONER_LOADER_ID, null, this);
            search = intent.getStringExtra("test");
            //userName.setText(search); //set the text view to the user we received
            executeSearch(search);
        }
    }

    private void executeSearch(String search) {
        String searchURL = DataUtils.buildSearchURL(search);

        Bundle args = new Bundle(); //create a new bundle to be used by loader with our data and key
        args.putString("searchURL", searchURL);
        getSupportLoaderManager().restartLoader(SUMMONER_LOADER_ID, args,this); //load in content given by user arguments
    }

    @Override
    public Loader<SummonerStatsData> onCreateLoader(int i, Bundle bundle) {
        String searchURL = null;

        if (bundle != null) {
            searchURL = bundle.getString("searchURL");
        }
        //Log.d(TAG, "MY URL: " + searchURL);
        return new SearchLoader(this, searchURL);
    }

    @Override
    public void onLoadFinished(Loader<SummonerStatsData> loader, SummonerStatsData data) {
        loadPending.setVisibility(View.INVISIBLE);
        //Log.d(TAG, "loader finished loading");
        if (data != null) {
            //ArrayList<String> championImages = data.championImages;

            statsAdapter.updateSearchResults(data.championNames, data.championImages);
            //statsAdapter.updateSearchResults(championImages);

            loadError.setVisibility(View.INVISIBLE);
            statsView.setVisibility(View.VISIBLE);
        } else {
            statsView.setVisibility(View.INVISIBLE);
            loadError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<SummonerStats.SummonerStatsData> loader) {

    }

    public static class SummonerStatsData {
        ArrayList<String> championImages; //holds images URLs
        ArrayList<String> championNames; //holds names of images
        ArrayList<String> matchJSON; //holds JSON data for specific match information
    }

    static class SearchLoader extends AsyncTaskLoader<SummonerStatsData> {

        String searchURL; //URL for a user's information
        SummonerStatsData searchData;

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
        public SummonerStatsData loadInBackground() { //NEED TO PASS AN OBJECT THAT HOLDS CHAMP IDS AND THE JSON TO THE MATCHES TO GET WINRATE
            if (searchURL != null) {
                //String searchResults = null;
                SummonerStatsData searchResults = new SummonerStatsData();
                searchResults.championImages = new ArrayList<>();
                searchResults.championNames = new ArrayList<>();
                try {
                    String userResults = NetworkUtils.doHTTPGet(searchURL);
                    Log.d(TAG, "MY ACCOUNT URL: " + searchURL);
                    String accountID = DataUtils.getAccountID(userResults); //parse the accountID to construct matchlist URL
                    String matchListURL = DataUtils.buildMatchListURL(accountID);
                    Log.d(TAG, "MY MATCH LIST URL: " + matchListURL);
                    String matchList = NetworkUtils.doHTTPGet(matchListURL);

                    ArrayList<String> championIDs = DataUtils.getChampionID(matchList); //gets unique champion ids for given user
                    String championListURL = DataUtils.buildChampionListURL();
                    String championList = NetworkUtils.doHTTPGet(championListURL);
                    Log.d(TAG, "MY CHAMP LIST URL: " + championListURL);

                    for (int i = 0; i < championIDs.size(); i++) { //CHANGE THIS TO GET BACK THE SEARCHRESULT, NOT ANOTHER OBJECT
                        DataUtils.SearchResult champion = DataUtils.getChampionImage(championList, championIDs.get(i));
                        searchResults.championImages.add(champion.championImage);
                        searchResults.championNames.add(champion.championName);
                        //searchResults.championImages.add(DataUtils.getChampionImage(championList, championIDs.get(i)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return searchResults;
            } else {
                return null;
            }
        }

        public void deliverResult(SummonerStatsData data) {
            searchData = data;
            super.deliverResult(data);
        }
    }
}
