package com.example.szegedimenetrend;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteAdapter extends ArrayAdapter<String> {
    private boolean deleteMode;
    private HashSet<Integer> selectedItems = new HashSet<>();

    public FavoriteAdapter(Context context, List<String> favorites, boolean deleteMode) {
        super(context, android.R.layout.simple_list_item_multiple_choice, favorites);
        this.deleteMode = deleteMode;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);

        if (deleteMode) {
            view.setChecked(selectedItems.contains(position));
            view.setBackgroundColor(selectedItems.contains(position) ? Color.LTGRAY : Color.TRANSPARENT);
        } else {
            view.setChecked(false);
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    public Set<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }
}
