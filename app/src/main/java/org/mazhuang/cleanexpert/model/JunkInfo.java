package org.mazhuang.cleanexpert.model;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkInfo implements Comparable<JunkInfo> {
    public String name;
    public long size;
    public String packageName;
    public String path;
    public ArrayList<JunkInfo> children;
    public boolean isVisible;

    public boolean isChildItem() {
        return children == null;
    }

    @Override
    public int compareTo(JunkInfo another) {
        if (this.name != null && this.name.equals("系统缓存")) {
            return 1;
        }

        if (another.name != null && another.name.equals("系统缓存")) {
            return -1;
        }

        if (this.size > another.size) {
            return 1;
        } else if (this.size < another.size) {
            return -1;
        } else {
            return 0;
        }
    }
}
