package com.example.editpicdemo.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.editpicdemo.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 龑 on 2017/5/11.
 */

public class CommonUtils {

    public static int getScreenHeight(Activity activity) {

        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    public static int getScreenWidth(Activity activity) {

        return activity.getWindowManager().getDefaultDisplay().getWidth();

    }

    /**
     *
     * @param context
     * @param url
     * @param imageView
     * @param placeholderRes
     */
    public static void loadPicBitmap(Context context, String url, ImageView imageView, int placeholderRes){
        Glide.with(context).load(url).asBitmap().centerCrop().placeholder(placeholderRes).error(placeholderRes).
                diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
    }

    public static boolean checkPermisson(Context context, String name) {
        String permissionName = "";
        PackageManager pm = context.getPackageManager();
        if (name.equals("camera")) {
            permissionName = "android.permission.CAMERA";
        } else if (name.equals("contacts")) {
            permissionName = "android.permission.READ_CONTACTS";
        } else if (name.equals("location")) {
            permissionName = "android.permission.ACCESS_FINE_LOCATION";
        } else if (name.equals("audio")) {
            permissionName = "android.permission.RECORD_AUDIO";
        } else if(name.equals("write")){
            permissionName = "android.permission.WRITE_EXTERNAL_STORAGE";
        } else if(name.equals("files")){
            permissionName = "android.permission.MOUNT_UNMOUNT_FILESYSTEMS";
        } else if(name.equals("read")){
            permissionName = "android.permission.READ_EXTERNAL_STORAGE";
        }
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, "com.njjds.sac"));

        return permission;
    }

    public static void goToPermissionConfig(Context context){
        gotoMiuiPermission(context);
    }

    /**
     * 跳转到miui的权限管理页面
     */
    public static void gotoMiuiPermission(Context context) {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", context.getPackageName());
        try {
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission(context);

        }
    }
    /**
     * 跳转到魅族的权限管理系统
     */
    public static void gotoMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission(context);
        }
    }

    /**
     * 华为的权限管理页面
     */
    public static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }


    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

    /**
     * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static void loadPicBitmapAboutPicEditor(Context context,String url,ImageView imageView,int placeholderRes){
        //String picType = url.substring(url.length()-3,url.length());
        //if(picType.equals("gif")||picType.equals("GIF")||picType.equals("Gif")){
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).
                placeholder(placeholderRes).error(placeholderRes).into(imageView);
        //}else {
        //Glide.with(context).load(url).asBitmap().signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).
        //placeholder(placeholderRes).error(placeholderRes).into(imageView);
        //}
    }


    /**
     * 获取当前的时间字符串
     */
    public static String getCurrentDatetimeStrForPicEditor(){
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(now);
    }

}
