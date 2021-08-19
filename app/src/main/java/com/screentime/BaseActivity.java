package com.screentime;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Set the references from the widgets
     */
    public abstract void reference();
    public  void init(){};
    public abstract void setListenrs();
    public void intentvalue() {};
}
