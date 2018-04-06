package frielstudios.lolstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {
    private SearchView search; //object to handle user searches

    public static final String USERNAME_ID = "userID"; //id for the username sent to the SummonerStats activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        search = (SearchView)menu.findItem(R.id.search).getActionView();

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
}
