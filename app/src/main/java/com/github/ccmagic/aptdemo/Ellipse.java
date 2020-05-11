package com.github.ccmagic.aptdemo;

import android.util.Log;

import com.github.ccmagic.apt.annotation.IShape;
import com.github.ccmagic.apt.annotation.SimpleFactory;

@SimpleFactory(name = "Ellipse", classType = "com.github.ccmagic.apt.annotation.IShape")
public class Ellipse implements IShape {
    private static final String TAG = "Ellipse";

    @Override
    public void draw() {
        Log.i(TAG, "draw: ");
    }
}
