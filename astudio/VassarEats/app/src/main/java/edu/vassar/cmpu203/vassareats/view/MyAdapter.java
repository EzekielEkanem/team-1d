package edu.vassar.cmpu203.vassareats.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.vassar.cmpu203.vassareats.databinding.FoodItemBinding;
import edu.vassar.cmpu203.vassareats.model.FoodItem;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<FoodItem> foodItems;

    // Constructor for the adapter
    public MyAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use ViewBinding to inflate the item layout
        FoodItemBinding binding = FoodItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data from the list to the UI components
        holder.binding.itemName.setText(foodItems.get(position).getFoodItemName());
        holder.binding.itemDescription.setText(foodItems.get(position).getDietLabels().toString());
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the dataset
        return foodItems.size();
    }

    // ViewHolder to hold reference to views for each item
    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final FoodItemBinding binding;

        public MyViewHolder(@NonNull FoodItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
