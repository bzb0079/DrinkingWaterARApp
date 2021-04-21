package app.th.project.drinkingWaterAR.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import org.jetbrains.annotations.NotNull;



public final class PermissionUtil {
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final String DEVICE_LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION";
    private static String[] appPermissions = new String[]{CAMERA_PERMISSION, DEVICE_LOCATION_PERMISSION};


    public static final boolean hasRequiredPermissions(@NotNull Activity activity) {
         for(String s: appPermissions){
             if (ActivityCompat.checkSelfPermission(activity,s) != PackageManager.PERMISSION_GRANTED) {
                 return false;
             }
         }
        return true;
    }

    public static final void requestRequiredPermissions(@NotNull Activity activity) {
        ActivityCompat.requestPermissions(activity, appPermissions,1);
    }

    public static final boolean shouldShowRequestPermissionRationale(@NotNull Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
    }

    public static final void startPermissionSettings(@NotNull Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String)null));
        activity.startActivity(intent);
    }


}
