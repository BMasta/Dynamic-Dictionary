package com.android.dynamic_dictionary;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class VariationFragment extends Fragment {
    private WordEntryVariation variation;
    ViewPager2 pagerMeanings;

    public VariationFragment(WordEntryVariation variation) {
        this.variation = variation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_variation_fragment, container, false);
        pagerMeanings = container.findViewById(R.id.pagerMeanings);
        MeaningsPagerAdapter pagerAdapter = new MeaningsPagerAdapter(this.getContext(), variation.getMeanings());
        pagerMeanings.setAdapter(pagerAdapter);
        return v;
    }
}
