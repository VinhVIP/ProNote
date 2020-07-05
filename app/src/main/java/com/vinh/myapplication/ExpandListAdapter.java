package com.vinh.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ExpandModel> listHeader;
    private HashMap<ExpandModel, List<ExpandModel>> listChild;

    public ExpandListAdapter(Context context, List<ExpandModel> listHeader, HashMap<ExpandModel, List<ExpandModel>> listChild) {
        this.context = context;
        this.listHeader = listHeader;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (listChild.get(listHeader.get(groupPosition)) == null) return 0;
        return listChild.get(listHeader.get(groupPosition)).size();
    }

    @Override
    public ExpandModel getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public ExpandModel getChild(int groupPosition, int childPosition) {
        return listChild.get(listHeader.get(groupPosition)).get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        String headerTitle = getGroup(groupPosition).name;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expand_list_header, null);
        }
        TextView textView = convertView.findViewById(R.id.txtExpandHeader);
        textView.setText(headerTitle);

        if (isExpand) textView.setTypeface(null, Typeface.BOLD);
        else textView.setTypeface(null, Typeface.NORMAL);

        ImageView imgIcon = convertView.findViewById(R.id.imgListExpand);
        ImageView imgArrow = convertView.findViewById(R.id.imgExpandArrow);

        switch (groupPosition) {
            case 0:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_note));
                imgArrow.setVisibility(View.INVISIBLE);
                break;
            case 1:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black));
                imgArrow.setVisibility(View.INVISIBLE);
                break;
            case 2:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lock_row));
                imgArrow.setVisibility(View.INVISIBLE);
                break;
            case 3:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete_black));
                imgArrow.setVisibility(View.INVISIBLE);
                break;
            case 4:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_manage_type));
                imgArrow.setVisibility(View.INVISIBLE);
                break;
            case 5:
                imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_note));
                imgArrow.setVisibility(View.VISIBLE);
                imgArrow.setImageDrawable(context.getResources().getDrawable(isExpand ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down));
                break;
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        String childTitle = getChild(groupPosition, childPosition).name;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expand_list_child, null);
        }
        TextView textView = convertView.findViewById(R.id.txtExpandChild);
        textView.setText(childTitle);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
