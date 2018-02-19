package frielstudios.lolstats;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Cameron on 2/15/2018.
 */

public class DataUtils {

    final static String VERSION = "7.10.1"; //current version of game to pull data from

    final static String BASE_URL_SUMMONER = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"; //base url to get user by username

    final static String API_KEY = "RGAPI-10e89c9b-ae94-4d7e-ae3b-0d160a9c1fd3"; //api key to access data
    final static String API_PARAM = "api_key";

    public static class SearchResult implements Serializable {
        public String userName;
        public String summonerLevel;
        public String accountID;
        //public String htmlURL;
        //public int stars;
    }

    public static String buildSearchURL(String userName) {
        return Uri.parse(BASE_URL_SUMMONER + userName).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build().toString();
    }

    public static ArrayList<SearchResult> parseJSON(String searchResult) {
        try {
            ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();

            JSONObject holder = new JSONObject(searchResult);

            SearchResult result = new SearchResult();

            result.userName = holder.getString("name");
            result.summonerLevel = holder.getString("summonerLevel");
            result.accountID = holder.getString("accountId");

            searchResults.add(result);

            return searchResults;
        } catch (JSONException e) {
            return null;
        }
    }
}
