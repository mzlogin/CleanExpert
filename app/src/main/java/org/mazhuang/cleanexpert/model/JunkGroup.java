package org.mazhuang.cleanexpert.model;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkGroup {
    public static final int GROUP_PROCESS = 0;
    public static final int GROUP_CACHE = 1;
    public static final int GROUP_APPLEFT = 2;
    public static final int GROUP_ADV = 3;
    public static final int GROUP_APK = 4;

    public String name;
    public long size;
    public ArrayList<JunkInfo> children;
}
