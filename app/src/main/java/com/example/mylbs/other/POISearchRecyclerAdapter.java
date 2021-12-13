package com.example.mylbs.other;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.mylbs.Activity.MarkActivity;
import com.example.mylbs.R;

import java.util.List;

public class POISearchRecyclerAdapter extends RecyclerView.Adapter<POISearchRecyclerAdapter.ViewHolder>{

    private ViewHolder viewHolder;
    private List<PoiInfo> infoList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView poiName;
        TextView poiAddress;
        ImageView plan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poiName = (TextView) itemView.findViewById(R.id.poi_name);
            poiAddress = (TextView) itemView.findViewById(R.id.poi_address);
            plan = (ImageView) itemView.findViewById(R.id.plan);
        }
    }

    public POISearchRecyclerAdapter(List<PoiInfo> infoList) {
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poi_search_list_item, viewGroup, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PoiInfo poiInfo = infoList.get(i);
        viewHolder.poiName.setText(poiInfo.getName());
        viewHolder.poiAddress.setText(poiInfo.address);
        viewHolder.poiName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = poiInfo.location;
                MarkActivity.actionStart(v.getContext(), latLng.latitude, latLng.longitude);
            }
        });
        viewHolder.poiAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = poiInfo.location;
                MarkActivity.actionStart(v.getContext(), latLng.latitude, latLng.longitude);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}
