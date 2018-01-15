package com.alibaba.android.vlayout.example;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J!nl!n on 2017/3/9.
 */
public class RootActivity extends ListActivity {

    private List<String> mTitles = new ArrayList<>();
    private List<ComponentName> mActivities = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            final String packageName = getPackageName();
            PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = info.activities;

            for (ActivityInfo activityInfo : activities) {
                String name = activityInfo.name;
                mTitles.add(name.substring(name.lastIndexOf(".")));
                mActivities.add(new ComponentName(packageName, name));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTitles));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.setComponent(mActivities.get(position));
        startActivity(intent);
    }

}
