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

public class Alldataadapter extends  Adapter<Alldataadapter.dataviewholder> {

    Activity activity;
    ArrayList<NewModel> getdata = new ArrayList<>();

    public Alldataadapter(FragmentActivity activity, ArrayList<NewModel> getdata) {
        this.activity = activity;
        this.getdata = getdata;

    }

    @NonNull
    @Override
    public dataviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.alldata, parent, false);
        return new dataviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Alldataadapter.dataviewholder holder, int position) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("MM dd, yyyy hh:mm:ss aa");

        holder.startresult.setText(sdf1.format(getdata.get(position).getStarttime()));

        holder.endresult.setText(sdf1.format(getdata.get(position).getEndtime()));

    }

    @Override
    public int getItemCount() {
        return getdata.size();
    }

    public class dataviewholder extends RecyclerView.ViewHolder {

        TextView startresult, endresult;

        public dataviewholder(View itemView) {
            super(itemView);

            startresult = itemView.findViewById(R.id.startresult);
            endresult = itemView.findViewById(R.id.endresult);
        }
    }
}
