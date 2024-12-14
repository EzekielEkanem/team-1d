package edu.vassar.cmpu203.vassareats.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.model.ParentItem;

public class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;

    private List<Object> items;

    public ExpandableRecyclerViewAdapter(List<ParentItem> parentItems) {
        this.items = new ArrayList<>();
        for (ParentItem parent : parentItems) {
            items.add(parent);
            if (parent.isExpanded()) {
                items.addAll(parent.getChildItems());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ParentItem) {
            return TYPE_PARENT;
        } else {
            return TYPE_CHILD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PARENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_group, parent, false);
            return new ParentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_child, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PARENT) {
            ParentItem parentItem = (ParentItem) items.get(position);
            ParentViewHolder parentHolder = (ParentViewHolder) holder;
            parentHolder.parentTitle.setText(parentItem.getTitle());

            parentHolder.itemView.setOnClickListener(v -> {
                parentItem.setExpanded(!parentItem.isExpanded());
                updateItems();
            });
        } else {
            String childItem = (String) items.get(position);
            ChildViewHolder childHolder = (ChildViewHolder) holder;
            childHolder.childTitle.setText(childItem);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updateItems() {
        List<Object> updatedItems = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof ParentItem) {
                ParentItem parentItem = (ParentItem) item;
                updatedItems.add(parentItem);
                if (parentItem.isExpanded()) {
                    updatedItems.addAll(parentItem.getChildItems());
                }
            }
        }
        items = updatedItems;
        notifyDataSetChanged();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView parentTitle;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentTitle = itemView.findViewById(R.id.parentTextView);
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView childTitle;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            childTitle = itemView.findViewById(R.id.childTextView);
        }
    }
}
