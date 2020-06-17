package com.cszdlt.launchwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class TransparentAppWidget extends AppWidgetProvider {
    private final static String TAG = "TransparentAppWidget";
    private final static String CONFIG_PATH = Environment.getExternalStorageDirectory() + "/Config/";
    private final static String CONFIG_FILE_NAME = "config.json";
    private final static String CONFIG_LOG_FILE_NAME = "config.log";

    private final static String SEPARATE = "-_-";

    private static Gson gson = new Gson();
    private static JsonParser jsonParser = new JsonParser();

    public static void updateWidget(AppWidgetManager appWidgetManager, Context context, int appWidgetId,
                                    String className, String packageName) {
        PendingIntent pendingIntent;
        Intent startIntent;
        if (className == null || className.equals("null")) {
            startIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        } else {
            startIntent = new Intent();
            startIntent.setComponent(new ComponentName(packageName, className));
        }
        pendingIntent = PendingIntent.getActivity(
                context, appWidgetId, startIntent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.transparent_app_widget);
        views.setOnClickPendingIntent(R.id.transparent, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        JsonObject data = TransparentAppWidget.ReadFile(CreateFile(CONFIG_PATH + CONFIG_FILE_NAME));
        data.addProperty(String.valueOf(appWidgetId), className + SEPARATE + packageName);
        WriteFile(gson.toJson(data), CreateFile(CONFIG_PATH + CONFIG_FILE_NAME), false);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        JsonObject data = TransparentAppWidget.ReadFile(CreateFile(CONFIG_PATH + CONFIG_FILE_NAME));
        for (int i = 0; i < appWidgetIds.length; ++i) {
            String id = String.valueOf(appWidgetIds[i]);
            Log.d(TAG, "onUpdate: " + id);
            if (data.has(id)) {
                String packageInfo = data.get(id).getAsString();
                String name[] = packageInfo.split(SEPARATE);
                Log.d(TAG, "onUpdate: " + packageInfo + "  " + name.length);
                if (name.length == 2) {
                    updateWidget(appWidgetManager, context, appWidgetIds[i], name[0], name[1]);
                }
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        JsonObject data = TransparentAppWidget.ReadFile(CreateFile(CONFIG_PATH + CONFIG_FILE_NAME));
        for (int i = 0; i < appWidgetIds.length; ++i) {
            String id = String.valueOf(appWidgetIds[i]);
            Log.d(TAG, "onDeleted: " + id);
            if (data.has(id)) {
                data.remove(id);
            }
        }
        WriteFile(gson.toJson(data), CreateFile(CONFIG_PATH + CONFIG_FILE_NAME), false);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        JsonObject data = TransparentAppWidget.ReadFile(CreateFile(CONFIG_PATH + CONFIG_FILE_NAME));
        JsonObject newData = new JsonObject();
        for (int i = 0; i < oldWidgetIds.length; ++i) {
            String id = String.valueOf(oldWidgetIds[i]);
            Log.d(TAG, "onRestored: " + id);
            if (data.has(id)) {
                newData.addProperty(String.valueOf(newWidgetIds[i]), data.get(id).getAsString());
            }
        }
        WriteFile(gson.toJson(newData), CreateFile(CONFIG_PATH + CONFIG_FILE_NAME), false);
    }

    static private JsonObject ReadFile(File file) {
        FileInputStream inputFile = null;
        String msg = null;
        try {
            int len;
            inputFile = new FileInputStream(file);
            byte buffer[] = new byte[inputFile.available()];
            len = inputFile.read(buffer);
            inputFile.close();
            msg = new String(buffer, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (msg != null) {
            JsonElement jsonElement = jsonParser.parse(msg);
            if (jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
        }
        return new JsonObject();
    }

    static private void WriteFile(String data, File file, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            byte[] bytes = data.getBytes();
            fos.write(bytes, 0, bytes.length);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static private File CreateFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return null;
        }
        try {
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

