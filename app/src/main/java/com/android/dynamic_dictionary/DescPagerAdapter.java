package com.android.dynamic_dictionary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class DescPagerAdapter extends RecyclerView.Adapter<DescPagerAdapter.ViewPagerViewHolder> {
    private List<WordEntryMeaning> meanings = new ArrayList<>();

    public DescPagerAdapter(List<WordEntryMeaning> meanings) {
        this.meanings = meanings;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPartOfSpeech;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPartOfSpeech = itemView.findViewById(R.id.textViewPartOfSpeech);
        }
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_desc_fragment, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        holder.textViewPartOfSpeech.setText(meanings.get(position).getPartOfSpeech());
    }

    @Override
    public int getItemCount() {
        return meanings.size();
    }
}
