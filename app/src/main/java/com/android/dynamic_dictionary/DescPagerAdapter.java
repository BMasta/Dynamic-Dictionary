package com.android.dynamic_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DescPagerAdapter extends RecyclerView.Adapter<DescPagerAdapter.ViewPagerViewHolder> {
    private List<WordEntryMeaning> meanings;
    private Context parentContext;

    public DescPagerAdapter(Context context, List<WordEntryMeaning> meanings) {
        this.meanings = meanings;
        this.parentContext = context;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPartOfSpeech;
        private RecyclerView recyclerViewDefinitions;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPartOfSpeech = itemView.findViewById(R.id.textViewPartOfSpeech);
            recyclerViewDefinitions = itemView.findViewById(R.id.recycleViewDefinitions);
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
        holder.recyclerViewDefinitions.setAdapter(new DefinitionsRecycleViewAdapter(meanings.get(position).getDefinitions()));
        holder.recyclerViewDefinitions.setLayoutManager(new LinearLayoutManager(parentContext));
    }

    @Override
    public int getItemCount() {
        return meanings.size();
    }
}
