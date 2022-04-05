package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var buttons = Array(3) { Array<AppCompatButton?>(3) { null } }
    private var userTurn = true
    private var gameCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val resourceId = resources.getIdentifier("btn$i$j", "id", packageName)
                val button: AppCompatButton = findViewById(resourceId)
                buttons[i][j] = button
                button.text = ""
                button.setOnClickListener(clickListener)
            }
        }


        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private val clickListener = View.OnClickListener {
        performClick(it)
    }

    private fun performClick(it: View?): Boolean {
        val button = it as AppCompatButton

        if (button.text.isNotEmpty() && gameCounter >= 9) {
            return false
        }

        if (userTurn) {
            button.text = "X"
        } else {
            button.text = "0"
        }



        gameCounter++
        /* if (gameCounter < 9) {
             if (userTurn) {
                 scheduleSystemTurn()
             }
         }*/

        checkWinning()

        userTurn = !userTurn

        calculateForCpu()

        return true
    }

    private fun scheduleSystemTurn() {
        val randomNumber = (0..8).random()
        Log.e("ChildCount", binding.gridTicTac.childCount.toString())
        val button = binding.gridTicTac.getChildAt(randomNumber) as AppCompatButton
    }

    private fun calculateForCpu() {
        for (i in 0 until 3) {
            if (buttons[i][0]!!.text == buttons[i][1]!!.text && buttons[i][0]!!.text.isNotEmpty() && buttons[i][2]!!.text.isEmpty()) {
                performClick(binding.gridTicTac.getChildAt(2))
            }

            if (buttons[i][1]!!.text == buttons[i][2]!!.text && buttons[i][1]!!.text.isNotEmpty() && buttons[i][2]!!.text.isEmpty()) {
                performClick(binding.gridTicTac.getChildAt(2))
            }
        }
    }

    private fun checkWinning()
    {

    }
}
