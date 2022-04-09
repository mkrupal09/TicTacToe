package com.example.myapplication


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


class MyAccessibilityService : AccessibilityService() {


    var doNotListen: Boolean = false
    var handler = Handler()
    override fun onServiceConnected() {
        super.onServiceConnected()
        this.serviceInfo = serviceInfo.apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
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

        if (event?.source?.packageName != null && event.source.packageName == "com.android.systemui") {
            if (event != null) {
                if (event.source != null) {
                    makeChildInacessible(event.source!!)
                    /*observeEvent(event)*/
                }
            }
        } else {
            removeView()
        }

        /* if (doNotListen.not()) {
             event?.let {
                 if (event.source != null && event.source.packageName != null) {
                     if (event.source.packageName == "com.android.systemui" || event.source.packageName == "com.miui.securityadd") {
                         if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {

                             Log.e("Event", "X")

                             observeEventXiomi(rootInActiveWindow, event.source)

                             *//*observeEvent(event)*//*
                        }
                    }
                }
            }
        }*/
    }

    //For non mi devices

    private fun observeEvent(event: AccessibilityEvent) {
        if (event.source.contentDescription != null) {
            if (event.source.contentDescription.toString().lowercase()
                    .contains("bluetooth")
            ) {
                /*doNotListen = true

                (event.source.findAccessibilityNodeInfosByText("battery saver")[0] as AccessibilityNodeInfo).performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )

                performGlobalAction(GLOBAL_ACTION_HOME)
                sendBroadcasting("battery")

                Handler().postDelayed({
                    doNotListen = false
                    startActivity(
                        Intent(
                            this,
                            DoNotPerformActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })

                }, 500)*/
                addMyView(event.source)
            }
        }
    }


    private fun observeEventXiomi(
        parent: AccessibilityNodeInfo?,
        selectedNode: AccessibilityNodeInfo
    ) {
        for (i in 0 until parent!!.childCount) {
            val child: AccessibilityNodeInfo? = parent.getChild(i)
            if (child?.childCount ?: 0 > 0) {
                observeEventXiomi(child, selectedNode)
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
                                sendBroadcasting("wifi")
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

    private val list = arrayListOf<View>()
    private fun addMyView(node: AccessibilityNodeInfo) {

        /*for (i in list) {

        }*/

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val mLayout = FrameLayout(this)
        mLayout.setBackgroundColor(Color.TRANSPARENT)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.LEFT or Gravity.TOP


        val rect = Rect()
        node.getBoundsInScreen(rect)

        lp.x = rect.left
        lp.y = rect.top

        /*lp.x = 40
        lp.y = 400*/

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.layout_bar, mLayout)

        /*node.window.getBoundsInScreen(rect)*/
        /*mLayout.x = 20.0f
        mLayout.y = 500.0f*/

        /*mLayout.post {

        }*/

        /*mLayout.setOnClickListener {
            Log.e("MeClicked", "true")
            Toast.makeText(this@MyAccessibilityService, "Me Clicked", Toast.LENGTH_SHORT).show()
            handler.post {
                performGlobalAction(GLOBAL_ACTION_HOME)

                handler.postDelayed({
                    openActivity()
                    wm.removeView(mLayout)
                }, 1000)
            }
        }*/
        /* mLayout.setOnTouchListener { view, motionEvent ->
             return@setOnTouchListener false
         }*/


        /*removeView()*/

        wm.addView(mLayout, lp)
        list.add(mLayout)
    }

    private fun removeView() {
        for (i in list) {
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.removeView(i)
        }
        list.clear()
    }

    private fun killLauncher() {
        val name = "com.miui.securityadd"
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(name)
    }


    //For mi devices
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
                        val text = child.text

                        if (contentDes.isNullOrEmpty().not() && contentDes.toString().lowercase()
                                .contains("ultra") || (text.isNullOrEmpty()
                                .not() && text.toString().lowercase().contains("ultra")
                                    )
                        ) {


                            /* Log.e("MyAccessCordinates",rect.toShortString())*/

                            addMyView(child)

                            /*  child.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
                              child.parent.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)*/
                            /*addMyView()*/
                        }
                        Log.e("MyAccessName", name ?: "x")
                        Log.e("MyAccessDes", (contentDes ?: "y").toString())
                        Log.e("MyAccessText", (text ?: "z").toString())
                    }
                }
            }
        /*  if (isMeet.not()) {
              removeView()
          }*/

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

    fun sendBroadcasting(message: String) {

        val intent = Intent(MyForegroundService.BROADCAST_NAME)
        intent.putExtra("dataX", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}