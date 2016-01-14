package org.mazhuang.cleanexpert.util;

import android.content.Context;

/**
 * Created by mazhuang on 16/1/14.
 */
public class ContextUtil {
    public static Context applicationContext;

    /**
     * 获取 resource id 对应的字符串
     * @param resId R.string.xxx
     */
    public static String getString(int resId) {
        return applicationContext.getResources().getString(resId);
    }
}
