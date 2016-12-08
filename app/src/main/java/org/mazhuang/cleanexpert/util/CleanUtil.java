package org.mazhuang.cleanexpert.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import org.mazhuang.cleanexpert.MyApplication;
import org.mazhuang.cleanexpert.R;
import org.mazhuang.cleanexpert.model.JunkInfo;
import org.mazhuang.cleanexpert.ui.JunkCleanActivity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhuang on 16/1/14.
 */
public class CleanUtil {
    public static String formatShortFileSize(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = R.string.byte_short;
        if (result > 900) {
            suffix = R.string.kilo_byte_short;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.mega_byte_short;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.giga_byte_short;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.tera_byte_short;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.peta_byte_short;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return context.getResources().
                getString(R.string.clean_file_size_suffix,
                        value, context.getString(suffix));
    }

    public static void freeAllAppsCache(final Handler handler) {

        Context context = MyApplication.getInstance();

        File externalDir = context.getExternalCacheDir();
        if (externalDir == null) {
            return;
        }

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS);
        for (ApplicationInfo info : installedPackages) {
            String externalCacheDir = externalDir.getAbsolutePath()
                    .replace(context.getPackageName(), info.packageName);
            File externalCache = new File(externalCacheDir);
            if (externalCache.exists() && externalCache.isDirectory()) {
                deleteFile(externalCache);
            }
        }

        boolean hanged = true;
        try {
            Method freeStorageAndNotify = pm.getClass()
                    .getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            long freeStorageSize = Long.MAX_VALUE;

            freeStorageAndNotify.invoke(pm, freeStorageSize, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    Message msg = handler.obtainMessage(JunkCleanActivity.MSG_SYS_CACHE_CLEAN_FINISH);
                    msg.sendToTarget();
                }
            });
            hanged = false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (hanged) {
            Message msg = handler.obtainMessage(JunkCleanActivity.MSG_SYS_CACHE_CLEAN_FINISH);
            Bundle bundle = new Bundle();
            bundle.putBoolean(JunkCleanActivity.HANG_FLAG, true);
            msg.setData(bundle);
            msg.sendToTarget();
        }
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String name : children) {
                boolean suc = deleteFile(new File(file, name));
                if (!suc) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void killAppProcesses(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return;
        }

        ActivityManager am = (ActivityManager)MyApplication.getInstance()
                .getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }

    public static void freeJunkInfos(ArrayList<JunkInfo> junks, final Handler handler) {
        for (JunkInfo info : junks) {
            File file = new File(info.mPath);
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        Message msg = handler.obtainMessage(JunkCleanActivity.MSG_OVERALL_CLEAN_FINISH);
        msg.sendToTarget();
    }
}
