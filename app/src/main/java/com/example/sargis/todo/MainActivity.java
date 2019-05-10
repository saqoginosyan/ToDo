package com.example.sargis.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Item> itemList;
    private Item item;
    private TextView noTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        noTask = findViewById(R.id.no_task);
        FloatingActionButton addButton = findViewById(R.id.add_button);

        itemList = generateData();
        refreshList();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDialog();
            }
        });

        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                deleteOne(pos);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modifyOne(position);
            }
        });
    }

    private void deleteOne(int pos) {
        final int position = pos;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete item.");
        alert.setMessage(" Are you sure you want to delete the selected item ?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteOnePos(position);
                refreshList();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void addDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add one task.");
        alert.setMessage("Complete the information to add a task.");

        final EditText name = new EditText(this);
        name.setHint("Name...");

        final EditText text = new EditText(this);
        text.setHint("Task...");

        final CheckBox importantCheck = new CheckBox(this);
        importantCheck.setText("Important task");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String important;

                if (importantCheck.isChecked()) {
                    important = "y";
                } else {
                    important = "n";
                }

                if (name.length() > 0 || text.length() > 0) {
                    item = new Item(name.getText().toString(), text.getText().toString(), important);
                    addItem(item);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void modifyOne(final int position) {

        item = itemList.get(position);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Modify task.");
        alert.setMessage("Complete the information to modify this task.");

        final EditText name = new EditText(this);
        name.setText(item.getName());

        final EditText text = new EditText(this);
        text.setText(item.getTask());

        final CheckBox importantCheck = new CheckBox(this);
        importantCheck.setText("Important task");

        if (item.getImportant().equals("y")) {
            importantCheck.setChecked(true);
        }

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);


        alert.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String important;
                if (importantCheck.isChecked()) {
                    important = "y";
                } else {
                    important = "n";
                }

                if (name.length() > 0 || text.length() > 0) {
                    item = new Item(name.getText().toString(), text.getText().toString(), important);
                    modifyItem(position, item);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void refreshList() {
        ListAdapter adapter = new ListAdapter(MainActivity.this, itemList);
        listView.setAdapter(adapter);

        if (itemList.size() > 0) {
            noTask.setVisibility(View.INVISIBLE);
        } else {
            noTask.setVisibility(View.VISIBLE);
        }
    }

    private List<Item> generateData() {
        itemList = new ArrayList<>();
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData", null);

        if (myData != null) {
            try {
                JSONArray jsonArray = new JSONArray(myData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String data = jsonArray.getString(i);
                    String[] splitData = data.split("\\.");

                    itemList.add(new Item(splitData[0], splitData[1], splitData[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return itemList;
    }

    private void modifyItem(int position, Item e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData", null);

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(myData);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                jsonArray.remove(position);
            }
            jsonArray.put(e.getName() + "." + e.getTask() + "." + e.getImportant());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        itemList.remove(position);
        itemList.add(e);

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void addItem(Item e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData", null);

        JSONArray jsonArray = null;
        if (myData == null) {
            jsonArray = new JSONArray();
            jsonArray.put(e.getName() + "." + e.getTask() + "." + e.getImportant());
            itemList.add(e);
        } else {
            try {
                jsonArray = new JSONArray(myData);
                jsonArray.put(e.getName() + "." + e.getTask() + "." + e.getImportant());
                itemList.add(e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void deleteOnePos(int pos) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData", null);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(myData);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                jsonArray.remove(pos);
            }
            itemList.remove(pos);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }
}