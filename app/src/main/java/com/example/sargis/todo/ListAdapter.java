package com.example.sargis.todo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Item> {

    ListAdapter(Context context, List<Item> itemList) {
        super(context, 0, itemList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ItemViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.task = convertView.findViewById(R.id.task);
            convertView.setTag(viewHolder);
        }

        Item item = getItem(position);

        if (item != null) {
            if (item.getImportant().equals("y")) {
                viewHolder.task.setTextColor(Color.parseColor("#EEFF2F36"));
            }

            if (item.getImportant().equals("n")) {
                viewHolder.task.setTextColor(Color.parseColor("#ee54917e"));
            }
            viewHolder.name.setText(item.getName());
            viewHolder.task.setText(item.getTask());
        }

        return convertView;
    }

    private class ItemViewHolder {
        public TextView name;
        public TextView task;
    }
}

class Item {
    private String name;
    private String task;
    private String important;


    Item(String name, String task, String important) {
        this.name = name;
        this.task = task;
        this.important = important;
    }

    String getName() {
        return this.name;
    }

    String getTask() {
        return this.task;
    }

    String getImportant() {
        return this.important;
    }
}
