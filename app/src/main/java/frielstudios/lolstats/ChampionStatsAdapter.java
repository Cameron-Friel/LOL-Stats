package frielstudios.lolstats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Cameron on 4/27/2018.
 */

public class ChampionStatsAdapter extends RecyclerView.Adapter<ChampionStatsAdapter.ChampionStatsViewHolder> {

    public void updateSearchResults() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        /*if (championArtImages != null) {
            return championArtImages.size();
        } else {
            return 0;
        }*/
        return 0; //remove LATER
    }

    @Override
    public ChampionStatsAdapter.ChampionStatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.champ_art_template, parent, false);
        return new ChampionStatsAdapter.ChampionStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChampionStatsViewHolder holder, int position) {
        holder.bind();
    }

    class ChampionStatsViewHolder extends RecyclerView.ViewHolder {
        ImageView championImage;

        public ChampionStatsViewHolder(View itemView) {
            super(itemView);
            //championImage = (ImageView)itemView.findViewById(R.id.champSlideshowImage);

        }

        public void bind() {

        }
    }
}