package com.example.myretrofittest.util;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by gcy on 2017/6/2 0002.
 */
public class AndroidUtil {
    public static void showHintBar(View view, String str) {
        Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show();
    }

    public static void jumptoNextAct(Context packageContext, Class<?> cls) {
        packageContext.startActivity(new Intent(packageContext, cls));
    }
}
