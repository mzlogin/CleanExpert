package org.mazhuang.cleanexpert.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.RemoteException;

import org.mazhuang.cleanexpert.MyApplication;
import org.mazhuang.cleanexpert.R;
import org.mazhuang.cleanexpert.callback.IScanCallback;
import org.mazhuang.cleanexpert.model.JunkInfo;

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

    private IScanCallback mCallback;
    private int mScanCount;
    private int mTotalCount;
    private ArrayList<JunkInfo> mSysCaches;
    private HashMap<String, String> mAppNames;
    private long mTotalSize = 0L;

    public SysCacheScanTask(IScanCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mCallback.onBegin();
        PackageManager pm = MyApplication.getInstance().getPackageManager();
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS);

        IPackageStatsObserver.Stub observer = new PackageStatsObserver();

        mScanCount = 0;
        mTotalCount = installedPackages.size();
        mSysCaches = new ArrayList<>();
        mAppNames = new HashMap<>();

        for (int i = 0; i < mTotalCount; i++) {
            ApplicationInfo info = installedPackages.get(i);
            mAppNames.put(info.packageName, pm.getApplicationLabel(info).toString());
            getPackageInfo(info.packageName, observer);
        }

        return null;
    }

    public void getPackageInfo(String packageName, IPackageStatsObserver.Stub observer) {
        try {
            PackageManager pm = MyApplication.getInstance().getPackageManager();
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
            mScanCount++;
            if (packageStats == null || !succeeded) {
            } else {
                JunkInfo info = new JunkInfo();
                info.mPackageName = packageStats.packageName;
                info.name = mAppNames.get(info.mPackageName);
                info.mSize = packageStats.cacheSize + packageStats.externalCacheSize;
                if (info.mSize > 0) {
                    mSysCaches.add(info);
                    mTotalSize += info.mSize;
                }
                mCallback.onProgress(info);
            }

            if (mScanCount == mTotalCount) {
                JunkInfo info = new JunkInfo();
                info.name = MyApplication.getInstance().getString(R.string.system_cache);
                info.mSize = mTotalSize;
                Collections.sort(mSysCaches);
                Collections.reverse(mSysCaches);
                info.mChildren = mSysCaches;
                info.mIsVisible = true;
                info.mIsChild = false;

                ArrayList<JunkInfo> list = new ArrayList<>();
                list.add(info);
                mCallback.onFinish(list);
            }
        }
    }
}
