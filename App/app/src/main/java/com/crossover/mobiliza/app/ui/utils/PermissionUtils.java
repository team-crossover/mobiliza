package com.crossover.mobiliza.app.ui.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean startPermissionCheck(Activity activity, String permission, int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

}
