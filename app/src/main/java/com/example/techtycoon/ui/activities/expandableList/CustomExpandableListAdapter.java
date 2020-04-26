package com.example.techtycoon.ui.activities.expandableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.techtycoon.Device.DeviceAttribute;
import com.example.techtycoon.R;

import androidx.core.util.Pair;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<DeviceAttribute> keys;
    private LinkedList<Pair<DeviceAttribute, List<String>>> listData;
    public LinkedList<Pair<DeviceAttribute, ArrayList<String>>> results;

    public CustomExpandableListAdapter(Context context,
                List<DeviceAttribute> keys, LinkedList<Pair<DeviceAttribute,List<String>>> listData) {
        this.context = context;
        this.listData = listData;
        this.keys = keys;
        results=new LinkedList<>();
        for(DeviceAttribute a : keys){
            results.add(new Pair<>(a,new ArrayList<>()));
        }

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.listData.stream()
                .filter(p -> p.first==keys.get(listPosition))
                .collect(Collectors.toList()).get(0)
                .second.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childValue = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_child, null);
        }
        CheckBox expandedListItemCheckBox = (CheckBox) convertView
                .findViewById(R.id.expandedListItemCheckBox);
        expandedListItemCheckBox.setText(childValue);
        expandedListItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    results.get(listPosition).second.add(childValue);
                }else{
                    boolean activeFilter=results.stream().anyMatch(a->a.first==keys.get(listPosition) && a.second.contains(childValue));
                    if(activeFilter){
                        results.get(listPosition).second.remove(childValue);
                    }else{
                        ArrayList<String> arrayList=results
                                .stream()
                                .filter(a->a.first==keys.get(listPosition))
                                .collect(Collectors.toList()).get(0).second;
                        arrayList.addAll(
                                listData.stream()
                                .filter(a->a.first==keys.get(listPosition))
                                .collect(Collectors.toList()).get(0).second);
                        results.get(listPosition).second.remove(childValue);
                    }

                }
            }
        });
        expandedListItemCheckBox.setChecked(true);

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return (int) this.listData.stream()
                .filter(p -> p.first == keys.get(listPosition))
                .collect(Collectors.toList())
                .get(0).second.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.keys.get(listPosition).toString();
    }

    @Override
    public int getGroupCount() {return this.keys.size();}

    @Override
    public long getGroupId(int listPosition) {return listPosition; }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
