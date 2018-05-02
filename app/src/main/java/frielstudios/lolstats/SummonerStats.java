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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static android.content.ContentValues.TAG;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cameron on 2/15/2018.
 */

public class SummonerStats extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SummonerStats.SummonerStatsData>, SummonerStatsAdapter.OnSearchItemClickListener {

    private final static int SUMMONER_LOADER_ID = 0; //id to load user account
    public final static String NAME_KEY = "nameKey"; //key to name intent passed to ChampionStats Activity
    public final static String MATCH_LIST_KEY = "matchListKey"; //key to pass match list to ChampionStats Activity

    private RecyclerView statsView;

    private SummonerStatsAdapter statsAdapter;

    private String search; //string that stores the user's search string

    private TextView userName;
    private ImageView userRank;
    private TextView summonerLevel;

    private ProgressBar loadPending;
    private TextView loadError;

    public String matchList; //stores match list to avoid querying again

    //private ImageView championImage;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        userName = (TextView)findViewById(R.id.userName);
        userRank = (ImageView)findViewById(R.id.userRank);
        //summonerLevel = (TextView)findViewById(R.id.summonerLevel);
        //championImage = (ImageView)findViewById(R.id.champImage);

        statsView = (RecyclerView)findViewById(R.id.statsView);
        statsView.setLayoutManager(new LinearLayoutManager(this));
        statsView.setHasFixedSize(true);

        statsAdapter = new SummonerStatsAdapter(this);
        statsView.setAdapter(statsAdapter);

        loadPending = (ProgressBar)findViewById(R.id.loadPending);
        loadError = (TextView)findViewById(R.id.loadError);

        loadPending.setVisibility(View.VISIBLE); //display to user that their data is being fetched

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.USERNAME_ID)) {
            getSupportLoaderManager().initLoader(SUMMONER_LOADER_ID, null, this);
            search = intent.getStringExtra(MainActivity.USERNAME_ID);

            search = capitalizeString(search);
            if (search != null) {
                //set the username we received from the MainActivity
                userName.setText(search);
                executeSearch(search); //begin data aggregation
            }
            else { //user gave bad string
                loadPending.setVisibility(View.INVISIBLE);
                loadError.setVisibility(View.VISIBLE);
            }
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
            setRankImage(data.summonerRank);
            statsAdapter.updateSearchResults(data.championNames, data.championImages, data.championWinRates, data.championPerformances);

            matchList = data.matchList; //store matchList for less overhead in ChampionStats Activity

            loadError.setVisibility(View.INVISIBLE);
            statsView.setVisibility(View.VISIBLE);
        } else {
            statsView.setVisibility(View.INVISIBLE);
            loadError.setVisibility(View.VISIBLE);
        }
    }

    public void setRankImage(String rank) { //COULD ALSO BE PLAT, DIAMOND, MASTER, OR CHALLENGER | MUST ADD THIS
        if (rank.equals("BRONZE")) {
            Glide.with(this).load(R.drawable.bronze).apply(new RequestOptions().override(500, 500))
                    .into(userRank);
        }
        else if (rank.equals("SILVER")) {
            Glide.with(this).load(R.drawable.silver).apply(new RequestOptions().override(500, 500))
                    .into(userRank);
        }
        else {
            Glide.with(this).load(R.drawable.gold).apply(new RequestOptions().override(500, 500))
                    .into(userRank);
        }
    }

    @Override
    public void onLoaderReset(Loader<SummonerStats.SummonerStatsData> loader) {

    }

    @Override
    public void onChampionClick(String name) { //create new intent on click of recycler view item
        Intent championIntent = new Intent(this, ChampionStats.class);
        championIntent.putExtra(NAME_KEY, name);
        championIntent.putExtra(SummonerStats.MATCH_LIST_KEY, matchList);
        startActivity(championIntent);
    }

    public static class SummonerStatsData {
        ArrayList<String> championImages; //holds images URLs
        ArrayList<String> championNames; //holds names of images
        ArrayList<Integer> championWinRates; //holds percentage of wins for given champion
        ArrayList<Integer> championPerformances; //holds whether the user is doing well on the champion or not
        String summonerRank; //rank of the user's account
        String matchList;
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
                    System.out.println("Heat" + searchData);
                    deliverResult(searchData);
                }
                else {
                    forceLoad();
                }
            }
        }

        private int determinePerformance(Integer winRate) { //give user a performance indicator on champion based off of their win rate
            if (winRate > 50) {
                return R.drawable.flame; //user is playing well on champion
            }
            else {
                return R.drawable.snow_flake; //user in under performing on champion
            }
        }

        private int calculateWinRate(double wins, double totalGames) { //calculates win rate based off of amount of wins and games played
            int winRate = 0;
            float tempWinRate = (float) (wins / totalGames) * 100;

            if (tempWinRate == 1.0) { //check to see if win rate is 100%
                winRate = 100;
            } else if (tempWinRate == 0.0) { //check to see if win rate is 0%
                winRate = 0;
            } else { //user has a unique win rate to convert
                winRate = Math.round(tempWinRate);
            }
            return winRate;
        }

        @Override
        public SummonerStatsData loadInBackground() { //NEED TO PASS AN OBJECT THAT HOLDS CHAMP IDS AND THE JSON TO THE MATCHES TO GET WIN RATE
            if (searchURL != null) {
                //String searchResults = null;
                SummonerStatsData searchResults = new SummonerStatsData();
                searchResults.championImages = new ArrayList<>();
                searchResults.championNames = new ArrayList<>();
                searchResults.championWinRates = new ArrayList<>();
                searchResults.championPerformances = new ArrayList<>();

                try {
                    String userResults = NetworkUtils.doHTTPGet(searchURL);
                    Log.d(TAG, "MY ACCOUNT URL: " + searchURL);
                    String accountID = DataUtils.getAccountID(userResults); //parse the accountID to construct match list URL
                    String matchListURL = DataUtils.buildMatchListURL(accountID);
                    Log.d(TAG, "MY MATCH LIST URL: " + matchListURL);
                    String matchList = NetworkUtils.doHTTPGet(matchListURL);
                    searchResults.matchList = matchList; //store matchList for less overhead later

                    ArrayList<String> championIDs = DataUtils.getChampionID(matchList); //gets unique champion ids for given user
                    String championListURL = DataUtils.buildChampionListURL();
                    String championList = NetworkUtils.doHTTPGet(championListURL);
                    Log.d(TAG, "MY CHAMP LIST URL: " + championListURL);

                    if (championIDs == null) { //could not retrieve data from the API
                        return null;
                    }

                    for (int i = 0; i < championIDs.size(); i++) { //CHANGE THIS TO GET BACK THE SEARCH RESULT, NOT ANOTHER OBJECT
                        DataUtils.SearchResult champion = DataUtils.getChampionImage(championList, championIDs.get(i));
                        searchResults.championImages.add(champion.championImage);
                        searchResults.championNames.add(champion.championName);
                    }

                    double wins = 0.0; //holds the amount of wins on a given champion
                    double totalGames = 0.0; //holds amount of games played on champion

                    for (int i = 0; i < championIDs.size(); i++) { //iterate through all champions user has played in ranked
                        String matchURL = DataUtils.buildChampionMatchesURL(accountID, championIDs.get(i)); //create url for specific champion match list
                        String championMatch = NetworkUtils.doHTTPGet(matchURL);
                        ArrayList<String> gameIDs = DataUtils.getChampionMatches(championMatch);

                        if (gameIDs == null) { //could not retrieve data from API
                            return null;
                        }

                        for (int j = 0; j < gameIDs.size(); j++) { //iterate through games to determine whether they won or not
                            String detailedMatchURL = DataUtils.buildDetailedChampionMatch(gameIDs.get(j));
                            String matchDetail = NetworkUtils.doHTTPGet(detailedMatchURL);
                            wins = DataUtils.getChampionMatchResult(matchDetail, accountID, wins);
                            totalGames++; //increment the total games played with champion being checked
                        }

                        Integer winRate = calculateWinRate(wins, totalGames);

                        int performance = determinePerformance(winRate);

                        searchResults.championWinRates.add(winRate);
                        searchResults.championPerformances.add(performance);
                        wins = 0; //reset winRate variable after specific champion ID is calculated
                        totalGames = 0;
                    }

                    String summonerID = DataUtils.getSummonerID(userResults);

                    //determine the user's rank
                    String summonerRankURL = DataUtils.buildRankURL(summonerID);
                    String summonerRankJSON = NetworkUtils.doHTTPGet(summonerRankURL);
                    searchResults.summonerRank = DataUtils.getUserRank(summonerRankJSON);

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

    public String capitalizeString(String s) { //capitalizes the first letter in a string
        if (s.length() > 0) { //check for length greater than 0, can't capitalize a string with no letters!
            String capitalizedString = s.substring(0, 1).toUpperCase() + s.substring(1);
            return capitalizedString;
        }
        else {
            return null;
        }
    }
}
