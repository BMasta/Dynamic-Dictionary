package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class VariationsPagerAdapter extends RecyclerView.Adapter<VariationsPagerAdapter.ViewPagerViewHolder> {
    private List<WordEntryVariation> variations;
    private final String word;
    private final String desc;
    private final Context parentContext;
    private final int LAYOUT_DESC = 0;
    private final int LAYOUT_VARIATION = 1;
    private final int LAYOUT_ERROR = 2;

    public VariationsPagerAdapter(Context context, List<WordEntryVariation> variations, String word, String description) {
        this.variations = variations;
        this.parentContext = context;
        this.word = word;
        this.desc = description;
    }

    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final ViewPager2 pagerMeanings;
        private final TabLayout tabLayout;
        private final TextView textViewVariation;
        private final TextView textViewDesc;
        private final TextView textViewError;
        private final ProgressBar progressBarConnection;
        private final Button buttonRetry;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerMeanings = itemView.findViewById(R.id.pagerMeanings);
            tabLayout = itemView.findViewById(R.id.tabLayoutVariations);
            textViewVariation = itemView.findViewById(R.id.textViewVariation);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            textViewError = itemView.findViewById(R.id.textViewMain_DictError);
            progressBarConnection = itemView.findViewById(R.id.progressBarMain_Dict);
            buttonRetry = itemView.findViewById(R.id.buttonMain_Retry);
        }
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutID = R.layout.layout_no_internet_fragment;
        switch (viewType) {
            case LAYOUT_DESC:
                layoutID = R.layout.layout_desc_fragment;
                break;
            case LAYOUT_VARIATION:
                layoutID = R.layout.layout_variation_fragment;
                break;
            case LAYOUT_ERROR:
                layoutID = R.layout.layout_no_internet_fragment;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        assert view != null;
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        if (position != 0) {
            if (variations.size() == 0) {
                // error item
                errorInit(holder);
            } else {
                // variation item
                variationInit(holder, position);
            }
        } else {
            // description item
            descInit(holder);
        }
    }

    @Override
    public int getItemCount() {
        if (variations.size() == 0)
            return 2;
        else
            return variations.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return LAYOUT_DESC;
        else if (variations.size() == 0)
            return LAYOUT_ERROR;
        else
            return LAYOUT_VARIATION;
    }

    private void variationInit(ViewPagerViewHolder holder, int position) {
        MeaningsPagerAdapter meaningsPagerAdapter = new MeaningsPagerAdapter(parentContext, variations.get(position - 1).getMeanings());
        holder.pagerMeanings.setAdapter(meaningsPagerAdapter);
        holder.pagerMeanings.setUserInputEnabled(false);
        new TabLayoutMediator(holder.tabLayout, holder.pagerMeanings, (tab, positionMeaning) ->
                tab.setText(variations.get(holder.getAdapterPosition() - 1)
                        .getMeanings().get(positionMeaning).getPartOfSpeech())).attach();
        String var = variations.get(position - 1).getWordVariation();
        if (var.length() > 1)
            var = var.substring(0, 1).toUpperCase() + var.substring(1);
        holder.textViewVariation.setText(var);
    }

    private void descInit(ViewPagerViewHolder holder) {
        if (desc != null)
            holder.textViewDesc.setText(desc);
    }

    private void errorInit(ViewPagerViewHolder holder) {
        holder.progressBarConnection.setVisibility(View.VISIBLE);
        holder.buttonRetry.setVisibility(View.INVISIBLE);
        holder.textViewError.setVisibility(View.GONE);
        holder.buttonRetry.setClickable(false);
        WebDictionary.sendDefinitionRequest(parentContext, word, holder);
        holder.buttonRetry.setOnClickListener(v -> {
            holder.progressBarConnection.setVisibility(View.VISIBLE);
            holder.buttonRetry.setVisibility(View.INVISIBLE);
            holder.buttonRetry.setClickable(false);
            WebDictionary.sendDefinitionRequest(parentContext, word, holder);
        });
    }

    public void applyDictUpdate(String word, String responseData, WebDictionary.Responses responseType, ViewPagerViewHolder holder) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ProgressBar progressBar = holder.progressBarConnection;
        Button button = holder.buttonRetry;
        TextView textView = holder.textViewError;

        progressBar.setVisibility(View.GONE);
        holder.buttonRetry.setClickable(true);
        switch (responseType) {
            case SUCCESS:
                variations = new WordEntry(word, desc, responseData).getWordVariations();
                notifyItemChanged(holder.getAdapterPosition());
                break;
            case NO_DATA:
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_undefined);
                break;
            case RESPONSE_ERROR:
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_dict);
                break;
            case NO_RESPONSE:
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_no_internet);
                break;
            default:
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setText(R.string.error_unknown);
        }
    }
}
