package com.screentime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WarninghDialogActivity extends AppCompatActivity {

    ImageView ivCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warningh_dialog);

        ivCancel = findViewById(R.id.ivCancel);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });

    }
}