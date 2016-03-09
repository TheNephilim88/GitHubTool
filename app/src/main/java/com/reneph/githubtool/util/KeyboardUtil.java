package com.reneph.githubtool.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Robert on 09.03.2016.
 */
public class KeyboardUtil {
    public static void hideKeyboard(Context ctx, Activity activity) {
        try {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ignored) {
        }
    }
}
