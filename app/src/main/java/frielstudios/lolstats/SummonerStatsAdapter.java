package frielstudios.lolstats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

/**
 * Created by Cameron on 2/18/2018.
 */

public class SummonerStatsAdapter extends RecyclerView.Adapter<SummonerStatsAdapter.StatsViewHolder> {
    private ArrayList<String> championImages; //holds data for each champion image
    private ArrayList<String> championNames; //holds name for each image
    private ArrayList<Integer> championWinRates; //holds win percentage for each champion
    private ArrayList<Integer> championPerformances; //holds performance indicator for each champion

    private OnSearchItemClickListener mChampionClickListener;

    SummonerStatsAdapter(OnSearchItemClickListener searchItemClickListener) {
       mChampionClickListener = searchItemClickListener;
    }

    public void updateSearchResults(ArrayList<String> championNames, ArrayList<String> championImages, ArrayList<Integer> championWinRates, ArrayList<Integer> championPerformances) {
        this.championNames = championNames;
        this.championImages = championImages;
        this.championWinRates = championWinRates;
        this.championPerformances = championPerformances;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (championImages != null) {
            return championImages.size();
        } else {
            return 0;
        }
    }

    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.stats_template, parent, false);
        return new StatsViewHolder(view);
    }

    public interface OnSearchItemClickListener {
        void onChampionClick(String name);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, int position) {
        //holder.bind(userList.get(position), image);
        holder.bind(championNames.get(position), championImages.get(position), championWinRates.get(position), championPerformances.get(position));
    }

    class StatsViewHolder extends RecyclerView.ViewHolder {
        private TextView championName; //holds name of championImage
        private TextView championWinRate; //holds win rate of champion
        private ImageView championImage; //holds image of champion
        private ImageView championPerformance; //holds image of performance icon

        public StatsViewHolder(View itemView) {
            super(itemView);
            championName = (TextView)itemView.findViewById(R.id.championName);
            championWinRate = (TextView)itemView.findViewById(R.id.championWinRate);
            championImage = (ImageView) itemView.findViewById(R.id.champImage);
            championPerformance = (ImageView)itemView.findViewById(R.id.championPerformance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = championNames.get(getAdapterPosition());
                    mChampionClickListener.onChampionClick(name);
                }
            });
        }

          public void bind(String name, String image, int winRate, int performance) { //set dynamic content into recycler view
            championName.setText(name);
            championWinRate.setText("\nWin Rate: " + winRate + "%");
            Glide.with(championImage.getContext()).load(image).apply(new RequestOptions().circleCrop().override(300, 300))
                    .into(championImage);
            Glide.with(championPerformance.getContext()).load(performance).apply(new RequestOptions().override(96, 96))
                    .into(championPerformance);
        }
    }
}
