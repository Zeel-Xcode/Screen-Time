package Fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.screentime.Alldataadapter;
import com.screentime.R;

import java.util.ArrayList;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;

public class AllDataFragment extends Fragment {

    ImageView  ic_export;
    Toolbar toolbar;
    TextView norecord, tvTitle;
    RecyclerView recyclerview;
    String appname;
    private DatabaseHandler2 databaseHandler2;
    LinearLayout llrecycler;

    public AllDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_data, container, false);

        ic_export = view.findViewById(R.id.ic_export);
        tvTitle = view.findViewById(R.id.tvTitle);
        toolbar = view.findViewById(R.id.toolbar);
        norecord = view.findViewById(R.id.norecord);
        recyclerview = view.findViewById(R.id.recyclerview);
        llrecycler = view.findViewById(R.id.llrecycler);
        databaseHandler2 = new DatabaseHandler2(getActivity());

        if (getArguments() != null){
            appname = getArguments().getString("which");
        }

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
        ArrayList<NewModel> getAppdata = new ArrayList<>();

        if (getdata.size() > 0){

            for (int i = 0; i < getdata.size(); i++) {
                if (getdata.get(i).getAppname().equals(appname)) {
                    getAppdata.add(0,getdata.get(i));
                }
            }

            if (getAppdata.size() > 0){
                llrecycler.setVisibility(View.VISIBLE);
                norecord.setVisibility(View.GONE);
                Alldataadapter adapter = new Alldataadapter(getActivity(),getAppdata);
                recyclerview.setAdapter(adapter);
            }else {
                llrecycler.setVisibility(View.GONE);
                norecord.setVisibility(View.VISIBLE);
            }

        } else {

        }

        return view;
    }

}