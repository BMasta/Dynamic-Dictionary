package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class VariationsPagerAdapter extends FragmentStateAdapter {
    private List<WordEntryVariation> variations;

    public VariationsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<WordEntryVariation> variations) {
        super(fragmentManager, lifecycle);
        this.variations = variations;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new VariationFragment(variations.get(position));
    }

    @Override
    public int getItemCount() {
       return variations.size();
    }
}
