package org.mazhuang.cleanexpert.callback;

import org.mazhuang.cleanexpert.model.JunkInfo;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public interface IScanCallback {
    void onBegin();

    void onProgress(JunkInfo info);

    void onFinish(ArrayList<JunkInfo> children);
}
