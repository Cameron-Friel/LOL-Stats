package frielstudios.lolstats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText playerSearch; //holds user player search string
    private Button buttonPlayerSearch; //button to search for player

    private final static int SUMMONER_LOADER_ID = 0; //id to loader

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerSearch = (EditText)findViewById(R.id.playerSearch);
        buttonPlayerSearch = (Button)findViewById(R.id.buttonPlayerSearch);

        buttonPlayerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = playerSearch.getText().toString();

                Intent summonerInfo = new Intent(v.getContext(), SummonerStats.class);
                summonerInfo.putExtra("test", search);
                startActivity(summonerInfo); //send user to new page with json data
            }
        });
    }
}
