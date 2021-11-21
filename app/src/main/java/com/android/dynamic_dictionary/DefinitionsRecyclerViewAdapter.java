package com.android.dynamic_dictionary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DefinitionsRecyclerViewAdapter extends RecyclerView.Adapter<DefinitionsRecyclerViewAdapter.RecycleViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final List<WordEntryDefinition> defs;
    private boolean isDefinition;

    public DefinitionsRecyclerViewAdapter(List<WordEntryDefinition> defs) {
        this.defs = defs;
        this.isDefinition = true;
    }

    @Override
    public void onClick(View v) {
        TextView textViewType = v.findViewById(R.id.textViewDefinition);
        TextView textViewDefinition = v.findViewById(R.id.textViewDefinitionBody);
        String body;
        if (isDefinition) {
            body = defs.get((int) v.getTag()).getExample();
            if (body == null || body.length() < 2)
                return;
            textViewType.setText(R.string.example);
        } else {
            body = defs.get((int) v.getTag()).getTitle();
            if (body == null || body.length() < 2)
                return;
            textViewType.setText(R.string.definition);
        }
        body = body.substring(0, 1).toUpperCase() + body.substring(1);
        textViewDefinition.setText(body);
        isDefinition = !isDefinition;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDefinition;

        public RecycleViewHolder(@NonNull View itemView, DefinitionsRecyclerViewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(adapter);
            textViewDefinition = itemView.findViewById(R.id.textViewDefinitionBody);
        }
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_definition_item, parent, false);
        return new DefinitionsRecyclerViewAdapter.RecycleViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String title = defs.get(position).getTitle();
        String body = title.substring(0, 1).toUpperCase() + title.substring(1);
        holder.textViewDefinition.setText(body);
    }

    @Override
    public int getItemCount() {
        return defs.size();
    }
}
