package com.example.findit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findit.MapsActivity;
import com.example.findit.Model.HistoryPlacesData;
import com.example.findit.Model.PlacesData;
import com.example.findit.R;
import com.example.findit.TimeLineView.TimeLineClassAdapter;
import java.util.ArrayList;

public class HistoryPlacesAdapter extends RecyclerView.Adapter<HistoryPlacesAdapter.MyViewHolder> {

    ArrayList<HistoryPlacesData> list;
    Context context;


    public HistoryPlacesAdapter(Context context, ArrayList<HistoryPlacesData> placesData){
        this.context = context;
        this.list = placesData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_recycler_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HistoryPlacesData data = new HistoryPlacesData();
        data = list.get(position);

        TimeLineClassAdapter adapter = new TimeLineClassAdapter(data.getPlacesData(),context);
        holder.recyclerViewLine.setAdapter(adapter);

        holder.txtDate.setText(data.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtDate;
        RecyclerView recyclerViewLine;
        LinearLayout parentLay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewLine = itemView.findViewById(R.id.recycler_view_timeline);
            recyclerViewLine.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            txtDate = itemView.findViewById(R.id.txt_date);
            parentLay = itemView.findViewById(R.id.parent_layout);
            listeners();
        }

        private void listeners() {
            parentLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<PlacesData> list2 = list.get(getAdapterPosition()).getPlacesData();
                    for (int i=0;i<list2.size()-2;i++){
                        for (int j=0;j<list2.size()-2;j++){
                            double lat1 = list2.get(j).getPlaceLatLng().latitude;
                            double long1 = list2.get(j).getPlaceLatLng().longitude;

                            double lat2 = list2.get(j+1).getPlaceLatLng().latitude;
                            double long2 = list2.get(j+1).getPlaceLatLng().longitude;

                            double lat3 = list2.get(j+2).getPlaceLatLng().latitude;
                            double long3 = list2.get(j+2).getPlaceLatLng().longitude;
                            if(distance(lat1,long1,lat2,long2,"K") > distance(lat1,long1,lat3,long3,"K")){
                                PlacesData temp = list2.get(j+1);
                                list2.set(j+1,list2.get(j+2));
                                list2.set(j+2,temp);
                            }
                        }
                    }

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putParcelableArrayListExtra("places",list2);
                    context.startActivity(intent);
                }
            });
        }
        private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
            if ((lat1 == lat2) && (lon1 == lon2)) {
                return 0;
            }
            else {
                double theta = lon1 - lon2;
                double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
                dist = Math.acos(dist);
                dist = Math.toDegrees(dist);
                dist = dist * 60 * 1.1515;
                if (unit == "K") {
                    dist = dist * 1.609344;
                } else if (unit == "N") {
                    dist = dist * 0.8684;
                }
                return (dist);
            }
        }

    }
}
