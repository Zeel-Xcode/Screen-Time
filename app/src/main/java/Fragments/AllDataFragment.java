package Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.screentime.Alldataadapter;
import com.screentime.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AllDataFragment extends Fragment {

    FloatingActionButton fab;
    ImageView  ic_export;
    Toolbar toolbar;
    TextView norecord, tvTitle;
    RecyclerView recyclerview;
    String appname;
    FrameLayout llrecycler;
    public static ProgressBar spinner;
    DatabaseHandler2 databaseHandler2;

    public AllDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_data, container, false);

        ProgressDialog progressDialog = new ProgressDialog(getContext());

        fab = view.findViewById(R.id.fab);
        ic_export = view.findViewById(R.id.ic_export);
        tvTitle = view.findViewById(R.id.tvTitle);
        toolbar = view.findViewById(R.id.toolbar);
        norecord = view.findViewById(R.id.norecord);
        recyclerview = view.findViewById(R.id.recyclerview);
        llrecycler = view.findViewById(R.id.llrecycler);
        spinner = (ProgressBar)view.findViewById(R.id.progressBar);

        databaseHandler2 = new DatabaseHandler2(getContext());


        if (getArguments() != null){
            appname = getArguments().getString("which");
        }

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
        ArrayList<NewModel> getAppdata = new ArrayList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_export_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView export = dialog.findViewById(R.id.export);
                export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        spinner.setVisibility(View.VISIBLE);
                        databaseHandler2.exportappdata(appname);

                    }
                });

                TextView cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

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