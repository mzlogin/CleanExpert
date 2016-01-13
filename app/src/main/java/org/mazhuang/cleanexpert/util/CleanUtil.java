package org.mazhuang.cleanexpert.util;

import android.content.Context;

import org.mazhuang.cleanexpert.R;

/**
 * Created by mazhuang on 16/1/14.
 */
public class CleanUtil {
    public static String formatShortFileSize(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = R.string.byteShort;
        if (result > 900) {
            suffix = R.string.kilobyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.megabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.gigabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.terabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = R.string.petabyteShort;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return context.getResources().
                getString(R.string.clean_file_size_suffix,
                        value, context.getString(suffix));
    }
}
