package org.mazhuang.cleanexpert.task;

import android.os.AsyncTask;
import android.os.Environment;

import org.mazhuang.cleanexpert.callback.IScanCallback;
import org.mazhuang.cleanexpert.model.JunkInfo;
import org.mazhuang.cleanexpert.util.ContextUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mazhuang on 16/1/23.
 */
public class OverallScanTask extends AsyncTask<Void, Void, Void> {
    private IScanCallback mCallback;
    private final int SCAN_LEVEL = 4;
    private JunkInfo mApkInfo;
    private JunkInfo mLogInfo;
    private JunkInfo mTmpInfo;

    public OverallScanTask(IScanCallback callback) {
        mCallback = callback;
        mApkInfo = new JunkInfo();
        mLogInfo = new JunkInfo();
        mTmpInfo = new JunkInfo();
    }

    private void travelPath(File root, int level) {
        if (root == null || !root.exists() || level > SCAN_LEVEL) {
            return;
        }

        File[] lists = root.listFiles();
        for (File file : lists) {
            if (file.isFile()) {
                String name = file.getName();
                JunkInfo info = null;
                if (name.endsWith(".apk")) {
                    info = new JunkInfo();
                    info.size = file.length();
                    // TODO 解析出 apk 文件详细内容
                    info.name = name;
                    info.path = file.getAbsolutePath();
                    info.isChild = false;
                    info.isVisible = true;
                    mApkInfo.children.add(info);
                    mApkInfo.size += info.size;
                } else if (name.endsWith(".log")) {
                    info = new JunkInfo();
                    info.size = file.length();
                    info.name = name;
                    info.path = file.getAbsolutePath();
                    info.isChild = false;
                    info.isVisible = true;
                    mLogInfo.children.add(info);
                    mLogInfo.size += info.size;
                } else if (name.endsWith(".tmp") || name.endsWith(".temp")) {
                    info = new JunkInfo();
                    info.size = file.length();
                    info.name = name;
                    info.path = file.getAbsolutePath();
                    info.isChild = false;
                    info.isVisible = true;
                    mTmpInfo.children.add(info);
                    mTmpInfo.size += info.size;
                }

                if (info != null) {
                    mCallback.onProgress(info);
                }
            } else {
                if (level < SCAN_LEVEL) {
                    travelPath(file, level + 1);
                }
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        mCallback.onBegin();

        File externalDir = Environment.getExternalStorageDirectory();
        if (externalDir != null) {
            travelPath(externalDir, 0);
        }

        ArrayList<JunkInfo> list = new ArrayList<>();

        if (mApkInfo.size > 0L) {
            Collections.sort(mApkInfo.children);
            Collections.reverse(mApkInfo.children);
            list.add(mApkInfo);
        }

        if (mLogInfo.size > 0L) {
            Collections.sort(mLogInfo.children);
            Collections.reverse(mLogInfo.children);
            list.add(mLogInfo);
        }

        if (mTmpInfo.size > 0L) {
            Collections.sort(mTmpInfo.children);
            Collections.reverse(mTmpInfo.children);
            list.add(mTmpInfo);
        }

        mCallback.onFinish(list);

        return null;
    }
}
