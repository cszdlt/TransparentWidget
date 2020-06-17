package com.cszdlt.launchwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static MyAccessibilityService actionService;

    public static void setActionService(MyAccessibilityService actionService) {
        MainActivity.actionService = actionService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.actionService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
        finish();
    }
}
