package frielstudios.lolstats;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Created by Cameron on 2/15/2018.
 */

public class DataUtils {

    final static String VERSION = "7.10.1"; //current version of game to pull data from

    final static String BASE_URL_SUMMONER = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"; //base url to get user by username

    final static String BASE_URL_MATCHLIST = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"; //base url to get matchlist of user's games

    final static String BASE_URL_CHAMPION_MATCHES = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"; //base url to get matches specified by champion
    final static String CHAMPION_PARAM = "champion";

    final static String BASE_URL_CHAMPION_MATCH = "https://na1.api.riotgames.com/lol/match/v3/matches/"; //base url to get specific match

    final static String BASE_URL_CHAMPION_IMAGE = "https://ddragon.leagueoflegends.com/cdn/8.3.1/img/champion/"; //base url to get champion image reference

    final static String BASE_URL_CHAMPION_LIST = "http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json"; //base url to get champion names and stats

    final static String API_KEY = "RGAPI-cd01f188-9e84-48d6-8120-366b892847d1"; //api key to access data
    final static String API_PARAM = "api_key";

    public static class SearchResult implements Serializable {
        public String championName;
        public String championImage;
    }

    public static String buildSearchURL(String userName) {
        return Uri.parse(BASE_URL_SUMMONER + userName).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build().toString();
    }

    public static String buildMatchListURL(String accountID) {
        return Uri.parse(BASE_URL_MATCHLIST + accountID).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build().toString();
    }

    public static String buildChampionMatchesURL(String accountID, String championID) {
        return Uri.parse(BASE_URL_CHAMPION_MATCHES + accountID).buildUpon().appendQueryParameter(CHAMPION_PARAM, championID)
                .appendQueryParameter(API_PARAM, API_KEY).build().toString();
    }

    public static String buildDetailedChampionMatch(String gameID) {
        return Uri.parse(BASE_URL_CHAMPION_MATCH + gameID).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build().toString();
    }

    public static String buildChampionListURL() {
        return Uri.parse(BASE_URL_CHAMPION_LIST).toString();
    }

    public static String getAccountID(String searchResult) {
        try {
            JSONObject holder = new JSONObject(searchResult);
            String result;

            result = holder.getString("accountId");

            return result;
        } catch (JSONException e) {
            return null;
        }
    }

    private static boolean checkDuplicateChampion(ArrayList<String> championList, JSONObject resultItem) {
        try {
            for (int i = 0; i < championList.size(); i++) {
                if (resultItem.getString("champion").equals(championList.get(i))) {
                    return false; //return false if champion already in list
                }
            }
            return true; //return true if champion not already in list
        } catch (JSONException e) {
            return false;
        }
    }

    public static ArrayList<String> getChampionID(String searchResults) {
        try {
            JSONObject searchResultsObj = new JSONObject(searchResults);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("matches");

            ArrayList<String> championList = new ArrayList<String>();

            for (int i = 0; i < searchResultsItems.length(); i++) {
                JSONObject resultItem = searchResultsItems.getJSONObject(i);

                if (resultItem.getString("queue").equals("420")) {
                    boolean check = checkDuplicateChampion(championList, resultItem);

                    if (check == true) {
                        championList.add(resultItem.getString("champion"));
                    }
                }
            }
            return championList;
        } catch (JSONException e) {
            return null;
        }
    }

    public static ArrayList<String> getChampionMatches(String matches) {
        try {
            JSONObject searchResultsObj = new JSONObject(matches);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("matches");

            ArrayList<String> championMatches = new ArrayList<String>();

            for (int i = 0; i < searchResultsItems.length(); i++) {
                JSONObject resultItem = searchResultsItems.getJSONObject(i);

                if (resultItem.getString("queue").equals("420")) {
                    championMatches.add(resultItem.getString("gameId"));
                }
            }
            return championMatches;
        } catch (JSONException e) {
            return null;
        }
    }

    public static double getChampionMatchResult(String matchDetails, String accountID, double wins) { //determines whether user won a specific match or not
        try {
            JSONObject searchResultsObj = new JSONObject(matchDetails);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("participantIdentities");

            for (int i = 0; i < searchResultsItems.length(); i++) {
                JSONObject resultItem = searchResultsItems.getJSONObject(i);

                if (resultItem.getJSONObject("player").getString("accountId").equals(accountID)) {
                    int participantId = resultItem.getInt("participantId"); //store the user's id

                    JSONArray teams = searchResultsObj.getJSONArray("teams"); //fetch array of game results

                    if (teams.getJSONObject(0).getString("win").equals("Win") && participantId < 5) { //check for user as blue team winning
                        wins++; //the user won, increment to keep track of how many wins total
                    }
                    else if (teams.getJSONObject(0).getString("win").equals("Fail") && participantId > 5) { //check for user as red team winning
                        wins++; //the user won, increment to keep track of how many wins total
                    }
                    else {
                        //do nothing, the user lost this game
                    }
                    break; //break, the user has been found
                }
            }
            return wins;
        } catch (JSONException e) {
            System.out.println("JSON CHAMPION MATCH RESULT EXCEPTION: " + e);
            return -1;
        }
    }

    public static SearchResult getChampionImage(String championList, String championID) { //ERROR, NAMES WITH SPACES ARE NOT RETRIEVED FOR IMAGES
           try {
               JSONObject holder = new JSONObject(championList);
               JSONObject holderItems = holder.getJSONObject("data");
               SearchResult champion = new SearchResult();

               for (Iterator<String> it = holderItems.keys(); it.hasNext(); ) { //iterate through champion json objects
                   String key = it.next();
                   JSONObject resultItem = holderItems.getJSONObject(key);

                   if (resultItem.getString("key").equals(championID)) {
                       champion.championName = key; //the key for the json object is also the name of the champion
                       break;
                   }
               }

               String tempName = champion.championName + ".png"; //construct string for champion image

               champion.championImage = Uri.parse(BASE_URL_CHAMPION_IMAGE + tempName).buildUpon().build().toString();

               return champion;
        } catch (JSONException e) {
            System.out.println(e);
            return null;
        }
    }
}
