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

import java.util.ArrayList;

import Model.NewModel;
import Model.UsageModel;
import SQLiteDatabase.DatabaseHandler2;

public class AllDataFragment extends Fragment {

    RecyclerView recyclerview;
    String appname;
    private DatabaseHandler2 databaseHandler2;

    public AllDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_data, container, false);

        recyclerview = view.findViewById(R.id.recyclerview);
        databaseHandler2 = new DatabaseHandler2(getActivity());

        if (getArguments() != null){
            appname = getArguments().getString("which");
        }

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
        ArrayList<NewModel> getAppdata = new ArrayList<>();

        if (getdata.size() > 0){

            for (int i = 0; i < getdata.size(); i++) {
                if (getdata.get(i).getAppname().equals(appname)){
                    getAppdata.add(getdata.get(i));
                }
            }

        }else {

        }

        Alldataadapter adapter = new Alldataadapter((FragmentActivity) getContext(),getAppdata);
        recyclerview.setAdapter(adapter);

        return view;
    }
}