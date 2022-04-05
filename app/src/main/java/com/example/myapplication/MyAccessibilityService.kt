package com.example.myapplication


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.*


class MyAccessibilityService : AccessibilityService() {


    var doNotListen: Boolean = false
    var handler = Handler()
    override fun onServiceConnected() {
        super.onServiceConnected()
        this.serviceInfo = serviceInfo.apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOWS_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            packageNames = arrayOf("com.android.systemui")
            feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL
            notificationTimeout = 100
        }


        Log.e("MyAccess", "On")
    }


    override fun onInterrupt() {

    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("MyAccess", "EventOn")
        Log.e("MyAccess", event?.packageName.toString() ?: "")
        Log.e("MyAccessAction", event?.action.toString())
        /*recursiveLoopChildrenX(rootInActiveWindow)*/

        event?.let {
            makeChildInacessible(event.source!!)
        }

        /*  if (doNotListen.not()) {
              event?.let {
                  if (event.source != null && event.source.packageName != null) {
                      if (event.source.packageName == "com.android.systemui" || event.source.packageName == "com.miui.securityadd") {
                          if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {

                              Log.e("Event", "X")

                              recursiveLoopChildren(rootInActiveWindow, event.source)

                              *//*if (event.source.contentDescription.toString().lowercase()
                                    .contains("battery")
                            ) {
                                doNotListen = true

                                (event.source.findAccessibilityNodeInfosByText("battery saver")[0] as AccessibilityNodeInfo).performAction(
                                    AccessibilityNodeInfo.ACTION_CLICK
                                )

                                performGlobalAction(GLOBAL_ACTION_HOME)

                                Handler().postDelayed({
                                    doNotListen = false
                                    startActivity(
                                        Intent(
                                            this,
                                            DoNotPerformActivity::class.java
                                        ).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        })

                                }, 500)
                            }*//*
                        }
                    }
                }
            }
        }*/
    }

    private fun recursiveLoopChildren(
        parent: AccessibilityNodeInfo?,
        selectedNode: AccessibilityNodeInfo
    ) {
        for (i in 0 until parent!!.childCount) {
            val child: AccessibilityNodeInfo? = parent.getChild(i)
            if (child?.childCount ?: 0 > 0) {
                recursiveLoopChildren(child, selectedNode)
            } else {
                if (child != null) {
                    val name = child.viewIdResourceName
                    val contentDes = child.contentDescription

                    if (contentDes.isNullOrEmpty().not() && (contentDes.toString().lowercase()
                            .contains("reading") || contentDes.toString().lowercase()
                            .contains("ultra"))
                    ) {
                        if (selectedNode == child.parent) {
                            doNotListen = true
                            Log.e("MyAccessUltra", "foundX")

                            selectedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            performGlobalAction(GLOBAL_ACTION_HOME)

                            handler.post {
                                openActivity()
                            }

                            Handler().postDelayed({
                                doNotListen = false
                                /*killLauncher()*/
                            }, 500)

                            Log.e("MyAccessUltra", "Starting Activity")
                        } else if (selectedNode == child) {
                            Log.e("MyAccessUltra", "found")
                        }
                    }
                    Log.e("MyAccessName", name ?: "x")
                    Log.e("MyAccessText", (contentDes ?: "y").toString())
                }
            }
        }

    }

    private fun killLauncher() {
        val name = "com.miui.securityadd"
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(name)
    }


    @SuppressLint("SoonBlockedPrivateApi")
    private fun makeChildInacessible(
        parent: AccessibilityNodeInfo
    ) {
        if (parent.packageName != null && parent.packageName == "com.android.systemui")
            for (i in 0 until parent.childCount) {
                val child: AccessibilityNodeInfo? = parent.getChild(i)
                if (child?.childCount ?: 0 > 0) {
                    makeChildInacessible(child!!)
                } else {
                    if (child != null) {
                        val name = child.viewIdResourceName
                        val contentDes = child.contentDescription

                        if (contentDes.isNullOrEmpty().not() && (contentDes.toString().lowercase()
                                .contains("reading") || contentDes.toString().lowercase()
                                .contains("ultra"))
                        ) {

                          /*  child.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
                            child.parent.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)*/
                        }
                        Log.e("MyAccessName", name ?: "x")
                        Log.e("MyAccessText", (contentDes ?: "y").toString())
                    }
                }
            }

    }

    private fun openActivity() {
        startActivity(
            Intent(
                this@MyAccessibilityService,
                DoNotPerformActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        Log.e("MyAccess", "KeyEventOn")
        return super.onKeyEvent(event)

    }

    fun getForegroundApp(): String? {
        var currentApp = "NULL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey())?.packageName ?: ""
                }
            }
        } else {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }
        return currentApp
    }
}