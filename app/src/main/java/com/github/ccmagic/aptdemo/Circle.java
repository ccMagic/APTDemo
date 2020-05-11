package com.github.ccmagic.aptdemo;

import android.util.Log;

import com.github.ccmagic.apt.annotation.SimpleFactory;

@SimpleFactory(name = "Circle", classType = "com.github.ccmagic.apt.annotation.IShape")
public class Circle extends Ellipse {
    private static final String TAG = "Circle";

    @Override
    public void draw() {
        Log.i(TAG, "draw: ");
    }
}
