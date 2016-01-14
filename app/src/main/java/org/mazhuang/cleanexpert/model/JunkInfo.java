package org.mazhuang.cleanexpert.model;

import org.mazhuang.cleanexpert.R;
import org.mazhuang.cleanexpert.util.ContextUtil;

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
    public boolean isVisible = false;
    public boolean isChild = true;

    @Override
    public int compareTo(JunkInfo another) {
        String top = ContextUtil.getString(R.string.system_cache);

        if (this.name != null && this.name.equals(top)) {
            return 1;
        }

        if (another.name != null && another.name.equals(top)) {
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
