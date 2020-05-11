package com.github.ccmagic.aptdemo;


import android.util.Log;

import com.github.ccmagic.apt.annotation.IShape;
import com.github.ccmagic.apt.annotation.SimpleFactory;

@SimpleFactory(name = "Rectangle", classType = "com.github.ccmagic.apt.annotation.IShape")
public class Rectangle implements IShape {
    private static final String TAG = "Rectangle";

    @Override
    public void draw() {
        Log.i(TAG, "draw: ");
    }
}
