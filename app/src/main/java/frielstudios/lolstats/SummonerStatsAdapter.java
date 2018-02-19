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
    private ArrayList<DataUtils.SearchResult> userList; //holds data for each character for a given user
    private String image;
    //private OnSearchItemClickListener mSeachItemClickListener;

    /*SummonerStatsAdapter(OnSearchItemClickListener searchItemClickListener) {
        mSeachItemClickListener = searchItemClickListener;
    }*/

    public void updateSearchResults(ArrayList<DataUtils.SearchResult> searchResultsList, String image) {
        userList = searchResultsList;
        this.image = image;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
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
        holder.bind(userList.get(position), image);
    }

    class StatsViewHolder extends RecyclerView.ViewHolder {
        private TextView userName; //holds the username
        private TextView summonerLevel; //holds user's level
        private ImageView championImage;

        public StatsViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            summonerLevel = (TextView) itemView.findViewById(R.id.summonerLevel);
            championImage = (ImageView) itemView.findViewById(R.id.champImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DataUtils.SearchResult searchResult = userList.get(getAdapterPosition());
                    //mSeachItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

        public void bind(DataUtils.SearchResult searchResult, String image) {
            userName.setText(searchResult.userName);
            summonerLevel.setText(searchResult.summonerLevel);
            Picasso.with(championImage.getContext()).load(image).resize(300, 300).into(championImage);
        }
    }
}
