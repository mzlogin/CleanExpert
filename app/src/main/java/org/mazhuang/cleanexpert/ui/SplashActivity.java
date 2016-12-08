package org.mazhuang.cleanexpert.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.mazhuang.cleanexpert.util.DiskStat;
import org.mazhuang.cleanexpert.util.MemStat;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final DiskStat diskStat = new DiskStat();
                final MemStat memStat = new MemStat(SplashActivity.this);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.PARAM_TOTAL_SPACE, diskStat.getTotalSpace());
                        intent.putExtra(MainActivity.PARAM_USED_SPACE, diskStat.getUsedSpace());
                        intent.putExtra(MainActivity.PARAM_TOTAL_MEMORY, memStat.getTotalMemory());
                        intent.putExtra(MainActivity.PARAM_USED_MEMORY, memStat.getUsedMemory());
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        thread.start();
    }
}
