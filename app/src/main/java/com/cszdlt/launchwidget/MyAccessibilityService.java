package com.cszdlt.launchwidget;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
        MainActivity.setActionService(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

}