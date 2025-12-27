package edu.vassar.cmpu203.vassareats.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vassar.cmpu203.vassareats.FirestoreHelper;
import edu.vassar.cmpu203.vassareats.MainActivity;
import edu.vassar.cmpu203.vassareats.R;
import edu.vassar.cmpu203.vassareats.model.Menu;

public class FoodMenuFragment extends Fragment implements IExpandableRecylerViewAdapter.Listener {

    private static final String ARG_MEAL_NAME = "MEAL_NAME";

    private Menu menu; // Reference to the central model
    private ExpandableRecyclerViewAdapter adapter;
    private FirestoreHelper firestoreHelper;
    private Set<String> likedItems = new HashSet<>();
    private String userId;

    /**
     * Factory method to create a new instance of this fragment.
     */
    public static FoodMenuFragment newInstance(String mealName) {
        FoodMenuFragment fragment = new FoodMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_NAME, mealName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get shared model from MainActivity
        this.menu = ((MainActivity) requireActivity()).getMenu();

        // Get current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.userId = user.getUid();
            this.firestoreHelper = new FirestoreHelper();
        } else {
            // Handle error case where user is somehow null
            Log.e("FoodMenuFragment", "User is not authenticated!");
            // Optionally, navigate back or show an error
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the same layout that FoodMenuActivity used
        return inflater.inflate(R.layout.fragment_food_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get meal name from arguments
        String mealName = getArguments() != null ? getArguments().getString(ARG_MEAL_NAME) : null;
        if (mealName == null) {
            Log.e("FoodMenuFragment", "Meal name not provided in arguments!");
            return;
        }

        // Back button inside fragment UI (explicit back control)
        View backBtn = view.findViewById(R.id.toolbar);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                // pop the fragment back stack to return to previous fragment
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // NOTE: Toolbar setup is now handled by MainActivity's layout, so we remove that code.
        // We could update the MainActivity's title if we wanted, but we'll keep it simple for now.

        // Load liked items from Firestore
        if (firestoreHelper != null) {
            firestoreHelper.loadUserLikedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> likedItemsFromFirestore) {
                    if (likedItemsFromFirestore != null) {
                        likedItems.addAll(likedItemsFromFirestore);
                        if (adapter != null) adapter.setLikedItems(likedItems);
                    }
                }
                @Override public void onFailure(Exception e) {
                    Log.e("FoodMenuFragment", "Failed to load liked items", e);
                }
            });
        }

        // Set up RecyclerView
        try {
            ArrayList<ParentItem> menuItems = menu.getItemsForMealType(mealName);
            RecyclerView recyclerView = view.findViewById(R.id.foodMenuRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            this.adapter = new ExpandableRecyclerViewAdapter(requireContext(), menuItems, this, likedItems);
            recyclerView.setAdapter(this.adapter);

        } catch (Exception e) {
            Log.e("FoodMenuFragment", "Error initializing menu items", e);
            Toast.makeText(requireContext(), "Error loading menu.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLikeClicked(String foodId, boolean isLiked) {
        if (userId == null) return;

        if (isLiked) likedItems.add(foodId); else likedItems.remove(foodId);

        firestoreHelper.saveUserLikedItems(requireContext(), userId, new ArrayList<>(likedItems));

        long change = isLiked ? 1 : -1;
        firestoreHelper.updateLikesCount(foodId, change, (success, e) -> {
            if (!success) Log.e("FoodMenuFragment", "Failed to update likes count", e);
        });
    }
}

