package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.screentime.Alldataadapter;
import com.screentime.R;

import java.util.ArrayList;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;

public class DateFragment extends Fragment {

    RecyclerView recyclerview;
    String appname;
    String date;
    private DatabaseHandler2 databaseHandler2;

    public DateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        recyclerview = view.findViewById(R.id.recyclerview);
        databaseHandler2 = new DatabaseHandler2(getActivity());

        if (getArguments() != null){
            appname = getArguments().getString("which");
            date = getArguments().getString("date");
        }

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
        ArrayList<NewModel> getAppdata = new ArrayList<>();

        if (getdata.size() > 0){

            for (int i = 0; i < getdata.size(); i++) {
                if (getdata.get(i).getAppname().equals(appname) && getdata.get(i).getCurrentdate().equals(date)){
                    getAppdata.add(0,getdata.get(i));
                }
            }

        }else {
            recyclerview.setVisibility(View.GONE);
        }

        Alldataadapter adapter = new Alldataadapter(getActivity(),getAppdata);
        recyclerview.setAdapter(adapter);

        return view;
    }
}