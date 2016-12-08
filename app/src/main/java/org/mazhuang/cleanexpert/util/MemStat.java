package org.mazhuang.cleanexpert.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by mazhuang on 2016/12/8.
 */

public class MemStat {
    private long mTotalMemory;
    private long mUsedMemory;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public MemStat(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);

        mTotalMemory = memInfo.totalMem;
        mUsedMemory = memInfo.totalMem - memInfo.availMem;
    }

    public long getTotalMemory() {
        return mTotalMemory;
    }

    public long getUsedMemory() {
        return mUsedMemory;
    }
}
