package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MeaningsPagerAdapter extends RecyclerView.Adapter<MeaningsPagerAdapter.ViewPagerViewHolder> {
    private final List<WordEntryMeaning> meanings;
    private final Context parentContext;

    public MeaningsPagerAdapter(Context context, List<WordEntryMeaning> meanings) {
        this.meanings = meanings;
        this.parentContext = context;
    }

    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerViewDefinitions;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewDefinitions = itemView.findViewById(R.id.recycleViewDefinitions);
        }
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meaning_fragment, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        holder.recyclerViewDefinitions.setAdapter(new DefinitionsRecyclerViewAdapter(meanings.get(position).getDefinitions()));
        holder.recyclerViewDefinitions.setLayoutManager(new LinearLayoutManager(parentContext));
    }

    @Override
    public int getItemCount() {
        return meanings.size();
    }
}
