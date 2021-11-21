package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class VariationsPagerAdapterNoNotes extends RecyclerView.Adapter<VariationsPagerAdapterNoNotes.ViewPagerViewHolder> {
    private List<WordEntryVariation> variations;
    private String word;
    private String desc;
    private Context parentContext;
    private final int LAYOUT_DESC = 0, LAYOUT_VARIATION = 1;

    public VariationsPagerAdapterNoNotes(Context context, List<WordEntryVariation> variations, String word, String description) {
        this.variations = variations;
        this.parentContext = context;
        this.word = word;
        this.desc = description;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final ViewPager2 pagerMeanings;
        private final TabLayout tabLayout;
        private final TextView textViewVariation;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerMeanings = itemView.findViewById(R.id.pagerMeanings);
            tabLayout = itemView.findViewById(R.id.tabLayoutVariations);
            textViewVariation = itemView.findViewById(R.id.textViewVariation);
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
        String var = variations.get(position).getWordVariation();
        if (var.length() > 1)
            var = var.substring(0, 1).toUpperCase() + var.substring(1);
        holder.textViewVariation.setText(var);
    }

    @Override
    public int getItemCount() {
        return variations.size();
    }
}
