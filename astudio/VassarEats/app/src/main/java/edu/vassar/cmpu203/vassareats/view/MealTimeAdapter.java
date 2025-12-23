package edu.vassar.cmpu203.vassareats.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.model.MealTime;

public class MealTimeAdapter extends RecyclerView.Adapter<MealTimeAdapter.MealTimeViewHolder> {

    private final List<MealTime> mealTimes;
    private final Listener listener;

    public interface Listener {
        void onMealTimeClick(MealTime mealTime);
    }

    public MealTimeAdapter(List<MealTime> mealTimes, Listener listener) {
        this.mealTimes = mealTimes;
        this.listener = listener;
    }

    // New: allow updating the list of mealTimes when the model changes
    public void setMealTimes(List<MealTime> newMealTimes) {
        if (this.mealTimes == null) return;
        this.mealTimes.clear();
        if (newMealTimes != null) {
            this.mealTimes.addAll(newMealTimes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_time, parent, false);
        return new MealTimeViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealTimeViewHolder holder, int position) {
        holder.bind(mealTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mealTimes.size();
    }


    public static class MealTimeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealBackground;
        private final TextView mealTypeName;
        private final TextView mealTypeTime;
        private final Button viewMenuButton;
        private MealTime mealTime;

        public MealTimeViewHolder(@NonNull View itemView, Listener listener) {
            super(itemView);
            mealBackground = itemView.findViewById(R.id.mealBackground);
            mealTypeName = itemView.findViewById(R.id.mealTypeName);
            mealTypeTime = itemView.findViewById(R.id.mealTypeTime);
            viewMenuButton = itemView.findViewById(R.id.viewMenuButton);

            viewMenuButton.setOnClickListener(v -> {
                if (mealTime != null) {
                    listener.onMealTimeClick(mealTime);
                }
            });
        }

        void bind(MealTime mealTime) {
            this.mealTime = mealTime;
            mealTypeName.setText(mealTime.getMealName());
            mealTypeTime.setText(mealTime.getTimeRange());
            mealBackground.setImageResource(mealTime.getBackgroundDrawableId());
        }
    }
}
