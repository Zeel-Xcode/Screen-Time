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

public class Alldataadapter extends  Adapter<Alldataadapter.dataviewholder> {

    Activity activity;

    public Alldataadapter(FragmentActivity activity) {
        this.activity = activity;

    }

    @NonNull
    @Override
    public dataviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.alldata, parent, false);
        return new dataviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Alldataadapter.dataviewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
