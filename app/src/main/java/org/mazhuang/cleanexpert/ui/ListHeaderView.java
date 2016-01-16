package org.mazhuang.cleanexpert.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mazhuang.cleanexpert.R;

public class ListHeaderView extends RelativeLayout {
    private Context context;
    public TextView tvSize;
    public TextView tvProgress;

    public ListHeaderView(Context context) {
        super(context);
        this.context = context;
        View view = LayoutInflater.from(this.context).inflate(R.layout.list_header_view, null);
        addView(view);
        tvSize = (TextView) findViewById(R.id.tv_total_size);
        tvProgress = (TextView) findViewById(R.id.tv_progress_msg);
    }
}