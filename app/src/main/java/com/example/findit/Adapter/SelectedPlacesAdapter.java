package com.example.findit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.findit.Model.PlacesData;
import com.example.findit.R;

import java.util.ArrayList;

public class SelectedPlacesAdapter extends RecyclerView.Adapter<SelectedPlacesAdapter.MyViewHolder> {

    public ArrayList<PlacesData> list;
    Context context;
    static int count = 1;
    OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(View view, int adapterPosition);
    }

    public SelectedPlacesAdapter(Context context, ArrayList<PlacesData> placesData,OnClickListener onClickListener){
        this.context = context;
        this.list = placesData;
        this.onClickListener = onClickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_place_recycler_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlacesData data = new PlacesData();
        data = list.get(position);

        holder.txtCounter.setText(String.valueOf(data.getCount()));
        count++;
        holder.txtDesc.setText(data.getPlaceName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(PlacesData obj) {
        list.add(obj);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCounter,txtDesc;
        ImageView btnDel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCounter = itemView.findViewById(R.id.txt_count);
            txtDesc = itemView.findViewById(R.id.txt_des);
            btnDel = itemView.findViewById(R.id.btn_del);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

}
