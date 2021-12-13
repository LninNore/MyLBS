package com.example.mylbs.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.mylbs.R;

import java.util.List;

public class POISearchListAdapter extends ArrayAdapter<PoiInfo> {

    private int resourceId;
    private ViewHolder viewHolder;

    private class ViewHolder {
        TextView poiName;
        TextView poiAddress;
    }

    public POISearchListAdapter(@NonNull Context context, int resource, @NonNull List<PoiInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PoiInfo poiInfo = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.poiName = (TextView) view.findViewById(R.id.poi_name);
            viewHolder.poiAddress = (TextView) view.findViewById(R.id.poi_address);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.poiName.setText(poiInfo.getName());
        viewHolder.poiAddress.setText(poiInfo.address);
        return view;
    }
}
