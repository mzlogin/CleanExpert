package org.mazhuang.cleanexpert.callback;

import org.mazhuang.cleanexpert.model.JunkInfo;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public interface IScanCallback {
    public void onBegin();

    public void onProgress(JunkInfo info);

    public void onFinish(ArrayList<JunkInfo> children);
}
