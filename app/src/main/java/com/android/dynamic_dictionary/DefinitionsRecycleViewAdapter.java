package com.android.dynamic_dictionary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DefinitionsRecycleViewAdapter extends RecyclerView.Adapter<DefinitionsRecycleViewAdapter.RecycleViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private List<WordEntryDefinition> defs;
    private boolean isDefinition;
    private RecycleViewHolder holderToFlip;

    public DefinitionsRecycleViewAdapter(List<WordEntryDefinition> defs) {
        this.defs = defs;
        this.isDefinition = true;
    }

    @Override
    public void onClick(View v) {
        TextView textViewType = v.findViewById(R.id.textViewDefinition);
        TextView textViewDefinition = v.findViewById(R.id.textViewDefinitionBody);
        String body;
        if (isDefinition) {
            body = defs.get((int)v.getTag()).getExample();
            if (body == null || body.length() < 2)
                return;
            textViewType.setText("Example");
        } else {
            body = defs.get((int)v.getTag()).getTitle();
            if (body == null || body.length() < 2)
                return;
            textViewType.setText("Definition");
        }
        textViewDefinition.setText(body.substring(0, 1).toUpperCase() + body.substring(1));
        isDefinition = !isDefinition;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDefinition, textViewType;

        public RecycleViewHolder(@NonNull View itemView, DefinitionsRecycleViewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(adapter);
            textViewType = itemView.findViewById(R.id.textViewDefinition);
            textViewDefinition = itemView.findViewById(R.id.textViewDefinitionBody);
        }
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_definition_item, parent, false);
        return new DefinitionsRecycleViewAdapter.RecycleViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String title = defs.get(position).getTitle();
        holder.textViewDefinition.setText(title.substring(0, 1).toUpperCase() + title.substring(1));
    }

    @Override
    public int getItemCount() {
        return defs.size();
    }
}
