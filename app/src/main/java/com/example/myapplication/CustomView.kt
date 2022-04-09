package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle)
{
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}