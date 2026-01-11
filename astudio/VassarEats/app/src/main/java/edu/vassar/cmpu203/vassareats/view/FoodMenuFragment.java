package edu.vassar.cmpu203.vassareats.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import edu.vassar.cmpu203.vassareats.model.FoodItem;
import edu.vassar.cmpu203.vassareats.model.Menu;

public class FoodMenuFragment extends Fragment implements IExpandableRecylerViewAdapter.Listener {

    private static final String ARG_MEAL_NAME = "MEAL_NAME";

    private Menu menu; // Reference to the central model
    private ExpandableRecyclerViewAdapter adapter;
    private FirestoreHelper firestoreHelper;
    private Set<String> likedItems = new HashSet<>();
    private Set<String> dislikedItems = new HashSet<>();
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
            Log.e("FoodMenuFragment", "User is not authenticated!");
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
            backBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        // Load liked/disliked items from Firestore
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

            firestoreHelper.loadUserDislikedItems(userId, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> dislikedFromFirestore) {
                    if (dislikedFromFirestore != null) {
                        dislikedItems.addAll(dislikedFromFirestore);
                        if (adapter != null) {
                            try {
                                adapter.setDislikedItems(dislikedItems);
                            } catch (Exception ignored) { }
                        }
                    }
                }
                @Override public void onFailure(Exception e) {
                    Log.e("FoodMenuFragment", "Failed to load disliked items", e);
                }
            });
        }

        // Set up RecyclerView and adapter, then request images for children
        try {
            ArrayList<ParentItem> menuItems = menu.getItemsForMealType(mealName);
            RecyclerView recyclerView = view.findViewById(R.id.foodMenuRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            this.adapter = new ExpandableRecyclerViewAdapter(requireContext(), menuItems, this, likedItems);
            recyclerView.setAdapter(this.adapter);

            // Apply disliked items if already loaded
            if (!dislikedItems.isEmpty()) {
                try {
                    adapter.setDislikedItems(dislikedItems);
                } catch (Exception ignored) { }
            }

            // Request image urls for all food children (safe against mixed child types)
            if (menuItems != null && firestoreHelper != null) {
                for (ParentItem parent : menuItems) {
                    if (parent == null) continue;
                    List<?> children = parent.getChildItems();
                    if (children == null) continue;
                    for (Object childObj : children) {
                        if (childObj instanceof FoodItem) {
                            FoodItem child = (FoodItem) childObj;
                            String prompt = buildNanobananaPrompt(child.getFoodItemName(), "Vassar College dining hall");
                            ensureImageForFood(child.getFoodId(), prompt);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FoodMenuFragment", "Error initializing menu items", e);
            Toast.makeText(requireContext(), "Error loading menu.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLikeClicked(String foodId, boolean isLiked) {
        if (userId == null || firestoreHelper == null) return;

        if (isLiked) likedItems.add(foodId); else likedItems.remove(foodId);

        firestoreHelper.saveUserLikedItems(requireContext(), userId, new ArrayList<>(likedItems));

        long change = isLiked ? 1 : -1;
        firestoreHelper.updateLikesCount(foodId, change, (success, e) -> {
            if (!success) Log.e("FoodMenuFragment", "Failed to update likes count", e);
        });
    }

    @Override
    public void onDislikeClicked(String foodId, boolean isDisliked) {
        if (userId == null || firestoreHelper == null) return;

        if (isDisliked) dislikedItems.add(foodId); else dislikedItems.remove(foodId);

        firestoreHelper.saveUserDislikedItems(requireContext(), userId, new ArrayList<>(dislikedItems));

        long change = isDisliked ? -1 : 1;
        firestoreHelper.updateLikesCount(foodId, change, (success, e) -> {
            if (!success) Log.e("FoodMenuFragment", "Failed to update likes count on dislike action", e);
        });
    }

    private String buildNanobananaPrompt(String foodName, @Nullable String diningContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("Photorealistic close-up of ").append(foodName);
        if (diningContext != null && !diningContext.isEmpty()) {
            sb.append(" as served at ").append(diningContext);
        } else {
            sb.append(" as served at Vassar College dining halls");
        }
        sb.append(". Show only the meal (or the meal inside a simple bowl or plate). ");
        sb.append("No people, no hands, no utensils, no text, no logos, and no other objects in the background. ");
        sb.append("Neutral, plain background (white or soft neutral), soft diffuse lighting, natural colors, shallow depth of field, ");
        sb.append("photorealistic, high detail, appetizing presentation, centered composition. ");
        sb.append("Prefer top-down or 45-degree angle. Aspect ratio 4:3.");
        return sb.toString();
    }

    private void ensureImageForFood(String foodId, String prompt) {
        if (firestoreHelper == null || foodId == null || adapter == null) return;

        firestoreHelper.loadImageForFood(foodId, new FirestoreHelper.FirestoreImageCallback() {
            @Override
            public void onSuccess(byte[] imageBytes) {
                if (imageBytes != null) {
                    // Image already exists → display it
                    requireActivity().runOnUiThread(() -> {
                        try {
                            adapter.setImageBytes(foodId, imageBytes);
                        } catch (Exception e) {
                            Log.w("FoodMenuFragment", "Failed to set image bytes on adapter", e);
                        }
                    });
                } else {
                    // No image → generate with Nanobanana
                    firestoreHelper.generateNanobananaImage(prompt, new FirestoreHelper.FirestoreImageCallback() {
                        @Override
                        public void onSuccess(byte[] generatedBytes) {
                            if (generatedBytes != null) {
                                // Save to Firestore
                                firestoreHelper.saveImageForFood(foodId, generatedBytes);

                                // Update UI
                                requireActivity().runOnUiThread(() -> {
                                    try {
                                        adapter.setImageBytes(foodId, generatedBytes);
                                    } catch (Exception e) {
                                        Log.w("FoodMenuFragment", "Failed to set generated image bytes on adapter", e);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FoodMenuFragment", "Image generation failed", e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FoodMenuFragment", "Failed to load image bytes", e);
            }
        });
    }
}
