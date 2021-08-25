package com.screentime;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Model.NewModel;

public class Alldataadapter extends RecyclerView.Adapter<Alldataadapter.DataViewHolder>{

    Activity activity;
    ArrayList<NewModel> getAppdata;

    public Alldataadapter(FragmentActivity activity, ArrayList<NewModel> getAppdata) {
        this.activity = activity;
        this.getAppdata = getAppdata;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.alldata,parent,false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aa");

        holder.startresult.setText(getAppdata.get(position).getStarttime());
        holder.endresult.setText(getAppdata.get(position).getEndtime());

    }

    @Override
    public int getItemCount() {
        return getAppdata.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        TextView startresult,endresult;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            startresult = itemView.findViewById(R.id.startresult);
            endresult = itemView.findViewById(R.id.endresult);
        }
    }

}
