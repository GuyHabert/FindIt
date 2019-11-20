package com.example.findit.TimeLineView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findit.Model.PlacesData;
import com.example.findit.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

public class TimeLineClassAdapter extends RecyclerView.Adapter<TimeLineClassAdapter.TimeLineViewHolder>{

    ArrayList<PlacesData> mFeedList = new ArrayList<>();
    Context context;

    public TimeLineClassAdapter (ArrayList<PlacesData> list,Context context){
        this.mFeedList = list;
        this.context = context;
    }
    @NonNull
    @Override
    public TimeLineClassAdapter.TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.timeline_viewholder, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineClassAdapter.TimeLineViewHolder holder, int position) {

        PlacesData p = mFeedList.get(position);

        holder.txtAddr.setText(p.getPlaceName());
        if(position == 0){

            holder.mTimelineView.setStartLineColor(R.color.invis,getItemViewType(position));
            holder.mTimelineView.setEndLineColor(R.color.colorAccent,getItemViewType(position));
        }else if(position == mFeedList.size()-1){
            holder.mTimelineView.setEndLineColor(R.color.invis,getItemViewType(position));
            holder.mTimelineView.setStartLineColor(R.color.colorAccent,getItemViewType(position));
        }else{
            holder.mTimelineView.setEndLineColor(R.color.colorAccent,getItemViewType(position));
            holder.mTimelineView.setStartLineColor(R.color.colorAccent,getItemViewType(position));
        }

    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder{
        public TimelineView mTimelineView;
        TextView txtAddr;

        public TimeLineViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
            txtAddr = itemView.findViewById(R.id.txt_addr);
        }
    }
}
