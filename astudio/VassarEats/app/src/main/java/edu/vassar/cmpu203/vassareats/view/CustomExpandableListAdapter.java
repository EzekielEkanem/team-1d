package edu.vassar.cmpu203.vassareats.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import edu.vassar.cmpu203.vassareats.R;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> parentList;
    private Map<String, List<String>> childList;

    public CustomExpandableListAdapter(Context context, List<String> parentList, Map<String, List<String>> childList) {
        this.context = context;
        this.parentList = parentList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String parent = parentList.get(groupPosition);
        return childList.get(parent).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String parent = parentList.get(groupPosition);
        return childList.get(parent).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_meal_type, parent, false);
        }
        TextView parentTextView = convertView.findViewById(R.id.mealTypeName);
        parentTextView.setText(parentList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_food_item, parent, false);
        }
        TextView childTextView = convertView.findViewById(R.id.foodItemName);
        childTextView.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

