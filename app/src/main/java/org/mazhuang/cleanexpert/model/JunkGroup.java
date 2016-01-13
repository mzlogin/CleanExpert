package org.mazhuang.cleanexpert.model;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkGroup {
    public static final int GROUP_CACHE = 0;
    public static final int GROUP_APPLEFT = 1;
    public static final int GROUP_ADV = 2;
    public static final int GROUP_APK = 3;

    public String name;
    public long size;
    public ArrayList<JunkInfo> children;
}
