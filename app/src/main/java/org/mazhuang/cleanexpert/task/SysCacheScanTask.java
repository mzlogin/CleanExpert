package org.mazhuang.cleanexpert.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.RemoteException;

import org.mazhuang.cleanexpert.R;
import org.mazhuang.cleanexpert.callback.IScanCallback;
import org.mazhuang.cleanexpert.model.JunkInfo;
import org.mazhuang.cleanexpert.util.ContextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mazhuang on 16/1/14.
 */
public class SysCacheScanTask extends AsyncTask<Void, Void, Void> {

    private IScanCallback callback;
    private int scanCount;
    private int totalCount;
    private ArrayList<JunkInfo> sysCaches;
    private HashMap<String, String> appNames;
    private long totalSize = 0L;

    public SysCacheScanTask(IScanCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        callback.onBegin();
        PackageManager pm = ContextUtil.applicationContext.getPackageManager();
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS);

        IPackageStatsObserver.Stub observer = new PackageStatsObserver();

        scanCount = 0;
        totalCount = installedPackages.size();
        sysCaches = new ArrayList<JunkInfo>();
        appNames = new HashMap<String, String>();

        for (int i = 0; i < totalCount; i++) {
            ApplicationInfo info = installedPackages.get(i);
            appNames.put(info.packageName, pm.getApplicationLabel(info).toString());
            getPackageInfo(info.packageName, observer);
        }

        return null;
    }

    public void getPackageInfo(String packageName, IPackageStatsObserver.Stub observer) {
        try {
            PackageManager pm = ContextUtil.applicationContext.getPackageManager();
            Method getPackageSizeInfo = pm.getClass()
                    .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(pm, packageName, observer);
        } catch (NoSuchMethodException e ) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private class PackageStatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats packageStats, boolean succeeded)
                throws RemoteException {
            scanCount++;
            if (packageStats == null || !succeeded) {
            } else {
                JunkInfo info = new JunkInfo();
                info.packageName = packageStats.packageName;
                info.name = appNames.get(info.packageName);
                info.size = packageStats.cacheSize + packageStats.externalCacheSize;
                if (info.size > 0) {
                    sysCaches.add(info);
                    totalSize += info.size;
                }
                callback.onProgress(totalCount, scanCount, info.packageName);
            }

            if (scanCount == totalCount) {
                JunkInfo info = new JunkInfo();
                info.name = ContextUtil.getString(R.string.system_cache);
                info.size = totalSize;
                Collections.sort(sysCaches);
                Collections.reverse(sysCaches);
                info.children = sysCaches;
                info.isVisible = true;
                info.isChild = false;

                ArrayList<JunkInfo> list = new ArrayList<>();
                list.add(info);
                callback.onFinish(list);
            }
        }
    }
}
