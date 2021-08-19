package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.screentime.Alldataadapter;
import com.screentime.R;

import Model.UsageModel;

public class AllDataFragment extends Fragment {

    RecyclerView recyclerview;
    String limit, average;
    UsageModel model;

    public AllDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_data, container, false);

        recyclerview = view.findViewById(R.id.recyclerview);

        Alldataadapter adapter = new Alldataadapter((FragmentActivity) getContext());
        recyclerview.setAdapter(adapter);

        return view;
    }
}