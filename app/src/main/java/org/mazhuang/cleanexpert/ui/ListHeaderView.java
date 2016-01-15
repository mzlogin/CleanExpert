package org.mazhuang.cleanexpert.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mazhuang.cleanexpert.R;

public class ListHeaderView extends RelativeLayout {
    private Context context;
    private TextView tvSize;
    private TextView tvProgress;

    public ListHeaderView(Context context) {
        super(context);
        this.context = context;
        View view = LayoutInflater.from(this.context).inflate(R.layout.list_header_view, null);
        addView(view);
        tvSize = (TextView) findViewById(R.id.tv_total_size);
        tvProgress = (TextView) findViewById(R.id.tv_progress_msg);
    }

    public void setSize(String text) {
        tvSize.setText(text);
    }

    public void setProgress(String text) {
        tvProgress.setText(text);
    }

    public void setSizeColor(int color) {
        tvSize.setTextColor(color);
    }
}