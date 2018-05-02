package frielstudios.lolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SearchView search; //object to handle user searches

    SharedPreferences sharedPreferences; //fetch the user's set default username to search

    private int slideshowIndex = 1;
    private int counter = 0;
    private ArrayList<String> championImages; //REPLACE WITH championImage ImageView
    final private int NUM_CHAMP_IMAGES = 5; //create slideshow with five champion arts
    private ImageView championImage; //object to hold reference to current image in the slideshow

    public static final String USERNAME_ID = "userID"; //id for the username sent to the SummonerStats activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up preference listener and set search view text
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setSearchName(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        /*for (int i = 0; i < NUM_CHAMP_IMAGES; i++) {

        }*/
        //KEEP THIS FOR NOW, ACTUALLY RANDOMLY LOOKUP CHAMPION IMAGES LATER :/
        championImages = new ArrayList<String>();
        championImages.add("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Aatrox_0.jpg");
        championImages.add("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Kayle_0.jpg");
        championImages.add("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Ahri_0.jpg");
        championImages.add("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Ezreal_0.jpg");
        championImages.add("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Veigar_0.jpg");

        championImage = (ImageView)findViewById(R.id.champSlideshowImage);

        Glide.with(this).load("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Aatrox_0.jpg").into(championImage);

        initSlideshow();
    }

    private void initSlideshow() { //background task which waits a certain amounts of time to update main UI thread
        final Handler handle = new Handler();
        final Runnable carousel = new Runnable() {
            @Override
            public void run() {
                if (slideshowIndex == 0) {
                    Glide.with(getApplicationContext()).load(championImages.get(counter))
                            .apply(new RequestOptions().dontAnimate()).into(championImage);

                    championImage.animate().alpha(1f).setDuration(2000); //make image appear
                    slideshowIndex = 1;
                    handle.postDelayed(this, 5000); //stall for five seconds after showing new image
                }
                else {
                    championImage.animate().alpha(0f).setDuration(2000); //make image disappear
                    slideshowIndex = 0;
                    handle.postDelayed(this, 2000); //stall for two seconds after removing image
                }

                if (counter == NUM_CHAMP_IMAGES - 1) { //if at end of image array, reset to beginning
                    counter = 0;
                }
                else {
                    counter++;
                }
            }
        };
        handle.postDelayed(carousel, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        search = (SearchView)menu.findItem(R.id.search).getActionView();

        search.onActionViewExpanded(); //expand search view so text can be written in
        search.setQuery(getSearchName(), false); //set the user's currently set name preference
        search.setQueryHint(getResources().getString(R.string.searchName)); //set query hint in search bar

        search.setOnQueryTextListener (
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange (String newText) {
                        //text has changed, apply suggestions for search
                        return false;
                    }
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //text submitted by user

                        //init new activity to display user stats based off of inputted username
                        Intent summonerInfo = new Intent(getApplicationContext(), SummonerStats.class);
                        summonerInfo.putExtra(USERNAME_ID, query);
                        startActivity(summonerInfo);
                        return false;
                    }
                });
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) { //listener for when preferences change
        setSearchName(sharedPreferences);
    }

    public void setSearchName(SharedPreferences preferences) { //retrieves user's most recent set name search and sets it in the search view
        String summonerName = sharedPreferences.getString(
                getString(R.string.summonerNameKey),
                getString(R.string.summonerNameValue)
        );

        if (search != null) { //check if search view has been created
            search.onActionViewExpanded();
            search.setQuery(summonerName, false); //set the user's set name preference
        }
    }

    public String getSearchName() { //retrieves the current summonerName
        String summonerName = sharedPreferences.getString(
                getString(R.string.summonerNameKey),
                getString(R.string.summonerNameValue)
        );
        return summonerName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_settings: //sends to settings activity for user preferences
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() { //destroy listener as activity is destroyed
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
