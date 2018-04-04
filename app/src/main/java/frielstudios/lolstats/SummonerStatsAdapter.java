package frielstudios.lolstats;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Cameron on 2/18/2018.
 */

public class SummonerStatsAdapter extends RecyclerView.Adapter<SummonerStatsAdapter.StatsViewHolder> {
    private ArrayList<String> championImages; //holds data for each champion image
    private ArrayList<String> championNames; //holds name for each image
    private ArrayList<Integer> championWinRates; //holds win percentage for each champion

    //private OnSearchItemClickListener mSeachItemClickListener;

    /*SummonerStatsAdapter(OnSearchItemClickListener searchItemClickListener) {
        mSeachItemClickListener = searchItemClickListener;
    }*/

    public void updateSearchResults(ArrayList<String> championNames, ArrayList<String> championImages, ArrayList<Integer> championWinRates) {
        this.championNames = championNames;
        this.championImages = championImages;
        this.championWinRates = championWinRates;
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
        void onSearchItemClick(DataUtils.SearchResult searchResult);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, int position) {
        //holder.bind(userList.get(position), image);
        holder.bind(championNames.get(position), championImages.get(position), championWinRates.get(position));
    }

    class StatsViewHolder extends RecyclerView.ViewHolder {
        private TextView championName; //holds name of championImage
        private TextView championWinRate; //holds win rate of champion
        private ImageView championImage; //holds image of champion

        public StatsViewHolder(View itemView) {
            super(itemView);
            championName = (TextView)itemView.findViewById(R.id.championName);
            championWinRate = (TextView)itemView.findViewById(R.id.championWinRate);
            championImage = (ImageView) itemView.findViewById(R.id.champImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DataUtils.SearchResult searchResult = userList.get(getAdapterPosition());
                    //mSeachItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

          public void bind(String name, String image, Integer winRate) {
            Picasso.with(championImage.getContext()).load(image).resize(300, 300).into(championImage);
            championName.setText(name);
            championWinRate.setText("\nWin Rate: " + winRate + "%");
        }
    }
}
