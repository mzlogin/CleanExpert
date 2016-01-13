package org.mazhuang.cleanexpert.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.mazhuang.cleanexpert.R;
import org.mazhuang.cleanexpert.callback.IScanCallback;
import org.mazhuang.cleanexpert.model.JunkGroup;
import org.mazhuang.cleanexpert.model.JunkInfo;
import org.mazhuang.cleanexpert.task.SysCacheScanTask;
import org.mazhuang.cleanexpert.util.CleanUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class JunkCleanActivity extends AppCompatActivity {

    public static final int MSG_SYS_CACHE_BEGIN = 0x1001;
    public static final int MSG_SYS_CACHE_POS = 0x1002;
    public static final int MSG_SYS_CACHE_FINISH = 0x1003;

    private Handler handler;

    private boolean isSysCacheFinish = false;

    private boolean isScanning = false;

    private BaseExpandableListAdapter adapter;
    private Hashtable<Integer, JunkGroup> junkGroups = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junk_clean);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_SYS_CACHE_BEGIN:
                        break;
                    case MSG_SYS_CACHE_POS:
                        break;
                    case MSG_SYS_CACHE_FINISH:
                        isSysCacheFinish = true;
                        checkFinish();
                        break;
                }
            }
        };

        resetState();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.junk_list);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);
        listView.setDividerHeight(0);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                JunkInfo info = (JunkInfo)adapter.getChild(groupPosition, childPosition);
                if (groupPosition == JunkGroup.GROUP_APK ||
                        info.isChildItem() ||
                        (groupPosition == JunkGroup.GROUP_ADV && !info.isChildItem() && info.path != null)) {
                    if (info.path != null) {
                        Toast.makeText(JunkCleanActivity.this, info.path, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int childrenInThisGroup = adapter.getChildrenCount(groupPosition);
                    for (int i = childPosition + 1; i < childrenInThisGroup; i++) {
                        JunkInfo child = (JunkInfo)adapter.getChild(groupPosition, i);
                        if (!child.isChildItem()) {
                            break;
                        }

                        child.isVisible = !child.isVisible;
                    }
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return junkGroups.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                if (junkGroups.get(groupPosition).children != null) {
                    return junkGroups.get(groupPosition).children.size();
                } else {
                    return 0;
                }
            }

            @Override
            public Object getGroup(int groupPosition) {
                return junkGroups.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return junkGroups.get(groupPosition).children.get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                GroupViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(JunkCleanActivity.this)
                            .inflate(R.layout.group_list, null);
                    holder = new GroupViewHolder();
                    holder.packageNameTv = (TextView)convertView.findViewById(R.id.tv_package_name);
                    holder.packageSizeTv = (TextView)convertView.findViewById(R.id.tv_package_size);
                    convertView.setTag(holder);
                } else {
                    holder = (GroupViewHolder)convertView.getTag();
                }
                convertView.setBackgroundColor(Color.rgb(0x61, 0xb2, 0x96));

                JunkGroup group = junkGroups.get(groupPosition);
                holder.packageNameTv.setText(group.name);
                holder.packageSizeTv.setText(CleanUtil.formatShortFileSize(JunkCleanActivity.this, group.size));

                convertView.setPadding(20, 0, 20, 0);

                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                JunkInfo info = junkGroups.get(groupPosition).children.get(childPosition);

                if (info.isVisible) {
                    ChildViewHolder holder;
                    if (convertView == null || convertView.getTag() == null) {
                        convertView = LayoutInflater.from(JunkCleanActivity.this)
                                .inflate(R.layout.item_list, null);
                        holder = new ChildViewHolder();
                        holder.junkTypeTv = (TextView) convertView.findViewById(R.id.tv_junk_type);
                        holder.junkSizeTv = (TextView) convertView.findViewById(R.id.tv_junk_size);
                        convertView.setTag(holder);
                    } else {
                        holder = (ChildViewHolder) convertView.getTag();
                    }

                    holder.junkTypeTv.setText(info.name);
                    holder.junkSizeTv.setText(CleanUtil.formatShortFileSize(JunkCleanActivity.this, info.size));

                    if (info.isChildItem()) {
                        convertView.setPadding(100, 0, 20, 0);
                        convertView.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
                    } else {
                        convertView.setPadding(60, 0, 20, 0);
                        convertView.setBackgroundColor(Color.rgb(0xe0, 0xd8, 0xb8));
                    }
                } else {
                    convertView = LayoutInflater.from(JunkCleanActivity.this)
                            .inflate(R.layout.item_null, null);
                }

                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };

        listView.setAdapter(adapter);

        if (!isScanning) {
            isScanning = true;
            startScan();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void resetState() {
        isScanning = false;

        isSysCacheFinish = false;

        junkGroups = new Hashtable<>();

        JunkGroup cacheGroup = new JunkGroup();
        cacheGroup.name = "缓存垃圾";
        cacheGroup.children = new ArrayList<>();
        junkGroups.put(JunkGroup.GROUP_CACHE, cacheGroup);
    }

    private void checkFinish() {
        if (isSysCacheFinish) {
            isScanning = false;
            // TODO 更新界面，扫描完成
            Toast.makeText(this, "扫描完成", Toast.LENGTH_LONG).show();
        }
    }

    private void startScan() {
        SysCacheScanTask sysCacheScanTask = new SysCacheScanTask(new IScanCallback() {
            @Override
            public void onBegin() {
                Message msg = handler.obtainMessage(MSG_SYS_CACHE_BEGIN);
                msg.sendToTarget();
            }

            @Override
            public void onProgress(int total, int pos, String msg) {

            }

            @Override
            public void onFinish(ArrayList<JunkInfo> children) {
                JunkGroup cacheGroup = junkGroups.get(JunkGroup.GROUP_CACHE);
                cacheGroup.children.addAll(children);
                Collections.sort(cacheGroup.children);
                Collections.reverse(cacheGroup.children);
                for (JunkInfo info : children) {
                    cacheGroup.size += info.size;
                }
                Message msg = handler.obtainMessage(MSG_SYS_CACHE_FINISH);
                msg.sendToTarget();
            }
        });
        sysCacheScanTask.execute();
    }

    public static class GroupViewHolder {
        public TextView packageNameTv;
        public TextView packageSizeTv;
    }

    public static class ChildViewHolder {
        public TextView junkTypeTv;
        public TextView junkSizeTv;
    }
}
