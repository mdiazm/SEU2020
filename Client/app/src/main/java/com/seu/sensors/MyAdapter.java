package com.seu.sensors;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

    private List<Sensor> sensorList = new ArrayList<>();
    private OnItemClickListener listener;

    public MyAdapter(ArrayList<Sensor> myDataSet, OnItemClickListener listener) {
        sensorList = myDataSet;
        listener = listener;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor, parent, false);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();
        params.width = parent.getMeasuredWidth() / 2 - 16 ;
        params.height = parent.getMeasuredHeight() / 3;
        params.setMargins(8,8,8,8);
        v.setLayoutParams(params);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.image.setImageResource(sensorList.get(position).getImage());
        holder.text.setText(sensorList.get(position).getName());
        if(sensorList.get(position).getState()) {
            holder.view.setBackgroundColor(holder.view.getResources().getColor(R.color.colorAccent));
        }
        holder.bind(sensorList.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;
        public View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            image = view.findViewById(R.id.image) ;
            text = view.findViewById(R.id.text) ;

        }

        public void bind(final Sensor item, final int position,  final OnItemClickListener listener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(item.getName() + " "  + position);
                    if (item.getState()) {
                        view.setBackgroundColor(view.getResources().getColor(R.color.card));
                    } else {
                        view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                    }
                    item.setState(!item.getState());
                }
            });
        }
    }
}
