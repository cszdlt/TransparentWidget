package com.cszdlt.launchwidget.config;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cszdlt.launchwidget.PermissionUtils;
import com.cszdlt.launchwidget.R;
import com.cszdlt.launchwidget.TransparentAppWidget;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ConfigActivity extends AppCompatActivity implements AppListItemAdapter.AdapterOnClick {
    private static final String TAG = "ConfigActivity";

    RecyclerView recyclerView;
    AppListItemAdapter adapter;

    public static class ItemData {
        public String packageName;
        public String className;
        public String appName;
        public Drawable icon;

        public ItemData(String packageName, String className, String appName, Drawable icon) {
            this.packageName = packageName;
            this.className = className;
            this.appName = appName;
            this.icon = icon;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestPermissionsIfNeed(this);

        setContentView(R.layout.activity_config);
        recyclerView = (RecyclerView) findViewById(R.id.app_list);
        recyclerView.setHasFixedSize(true);
        initRecycleView(initData());
    }

    private List<ItemData> initData() {

        final PackageManager pm = getPackageManager();

        List<PackageInfo> packs = pm.getInstalledPackages(0);
        Collections.sort(packs, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo o1, PackageInfo o2) {
                return Collator.getInstance(Locale.CHINESE).compare(
                        o1.applicationInfo.loadLabel(pm).toString(),
                        o2.applicationInfo.loadLabel(pm).toString());
            }
        });
        List<ItemData> list = new ArrayList<>();
        for (int i = 0; i < packs.size(); ++i) {
            PackageInfo info = packs.get(i);
            list.add(new ItemData(info.packageName, null,
                    info.applicationInfo.loadLabel(pm).toString(), info.applicationInfo.loadIcon(pm)));
        }

//        List<AppWidgetProviderInfo> widgetProviderInfos =
//                AppWidgetManager.getInstance(this).getInstalledProviders();
//        Collections.sort(widgetProviderInfos, new Comparator<AppWidgetProviderInfo>() {
//            @Override
//            public int compare(AppWidgetProviderInfo o1, AppWidgetProviderInfo o2) {
//                return Collator.getInstance(Locale.CHINESE).compare(
//                        o1.loadLabel(pm), o2.loadLabel(pm));
//            }
//        });

//        for (int i = 0; i < widgetProviderInfos.size(); ++i) {
//            AppWidgetProviderInfo info = widgetProviderInfos.get(i);
//            list.add(new ItemData(info.provider.getPackageName(), info.provider.getClassName(), info.loadLabel(pm),
//                    info.loadIcon(this, android.util.DisplayMetrics.DENSITY_MEDIUM)));
//        }

        return list;
    }

    private void initRecycleView(List<ItemData> dataList) {
        adapter = new AppListItemAdapter(this, dataList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onClick(String packageName, String className) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int mAppWidgetId = 0;
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        TransparentAppWidget.updateWidget(appWidgetManager, this, mAppWidgetId, className, packageName);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
