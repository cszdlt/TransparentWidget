package com.cszdlt.launchwidget;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    private static final int REQUEST_PERMISSION_CODE = 0x293;

    /**
     * 检查是否拥有权限
     */
    public static boolean hasPermission(Context context, String perm) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.getApplicationContext().checkSelfPermission(perm) != PackageManager.PERMISSION_DENIED) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * This method retrieves all the permissions declared in the application's manifest.
     * It returns a non null array of permisions that can be declared.
     *
     * @param activity the Activity necessary to check what permissions we have.
     * @return a non null array of permissions that are declared in the application manifest.
     */
    @NonNull
    private static synchronized String[] getManifestPermissions(@NonNull final Activity activity) {
        PackageInfo packageInfo = null;
        List<String> list = new ArrayList<>(1);
        try {
            Log.d("PermissionUtils", activity.getPackageName());
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager
                    .GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PermissionUtils", "A problem occurred when retrieving permissions", e);
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String perm : permissions) {
//                    Log.d(TAG, "Manifest contained permission: " + perm);
                    list.add(perm);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 批量申请权限（如果当前没有权限的话)。授权结果在onRequestPermissionsResult中异步处理
     */
    public static void requestPermissionsIfNeed(Activity activity) {
        String[] perms = getManifestPermissions(activity);
        if ((perms.length == 0)) {
            return;
        }
        List<String> needPerms = new ArrayList<>();
        for (String perm : perms) {
            if (!hasPermission(activity, perm)) {
                needPerms.add(perm);
            }
        }
        if (needPerms.size() == 0) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            activity.requestPermissions(needPerms.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }
    }
}
