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

    final static String BASE_URL_MATCHLIST = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"; //base url to get matchlist of user's games

    final static String BASE_URL_CHAMPION_IMAGE = "https://ddragon.leagueoflegends.com/cdn/8.3.1/img/champion/"; //base url to get champion image reference

    final static String BASE_URL_CHAMPION_LIST = "https://na1.api.riotgames.com/lol/static-data/v3/champions"; //base url to get champion list
    final static String LANGUAGE_PARAM = "locale";
    final static String LANGUAGE_VALUE = "en_US";
    final static String CONDITION_PARAM = "dataById";
    final static String CONDITION_VALUE = "true";

    final static String API_KEY = "RGAPI-2aac79a5-7a08-4a2e-b773-af72174e4e30"; //api key to access data
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

    public static String buildChampionListURL() {
        return Uri.parse(BASE_URL_CHAMPION_LIST).buildUpon().appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .appendQueryParameter(CONDITION_PARAM, CONDITION_VALUE).appendQueryParameter(API_PARAM, API_KEY).build().toString();
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

    public static SearchResult getChampionImage(String championList, String championID) { //ERROR, NAMES WITH SPACES ARE NOT RETRIEVED FOR IMAGES
           try {
               JSONObject holder = new JSONObject(championList);
               SearchResult champion = new SearchResult();

               champion.championName = holder.getJSONObject("data").getJSONObject(championID).getString("name");

               String tempName = champion.championName + ".png";

               champion.championImage = Uri.parse(BASE_URL_CHAMPION_IMAGE + tempName).buildUpon().build().toString();

               return champion;
        } catch (JSONException e) {
            return null;
        }
    }
}
