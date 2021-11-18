package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class VariationsPagerAdapter extends RecyclerView.Adapter<VariationsPagerAdapter.ViewPagerViewHolder> {
    private List<WordEntryVariation> variations;
    private Context parentContext;

    public VariationsPagerAdapter(Context context, List<WordEntryVariation> variations) {
        this.variations = variations;
        this.parentContext = context;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private ViewPager2 pagerMeanings;
        private TabLayout tabLayout;
        private TextView textViewVariation;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerMeanings = itemView.findViewById(R.id.pagerMeanings);
            tabLayout = itemView.findViewById(R.id.tabLayoutVariations);
            textViewVariation = itemView.findViewById(R.id.textViewWord);
        }
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_variation_fragment, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {

        MeaningsPagerAdapter meaningsPagerAdapter = new MeaningsPagerAdapter(parentContext, variations.get(position).getMeanings());
        holder.pagerMeanings.setAdapter(meaningsPagerAdapter);
        holder.pagerMeanings.setUserInputEnabled(false);
        new TabLayoutMediator(holder.tabLayout, holder.pagerMeanings, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int positionMeaning) {
                tab.setText(variations.get(holder.getAdapterPosition()).getMeanings().get(positionMeaning).getPartOfSpeech());
            }
        }).attach();
        holder.textViewVariation.setText(variations.get(position).getWordVariation());
    }

    @Override
    public int getItemCount() {
        return variations.size();
    }


}
