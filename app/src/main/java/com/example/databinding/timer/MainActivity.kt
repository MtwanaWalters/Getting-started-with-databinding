package com.example.databinding.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator


class MainActivity : AppCompatActivity() {

    // UI controls
    private lateinit var timerTextView: TextView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var floatingButton: ExtendedFloatingActionButton
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var resetButton: Button
    private lateinit var addTimerButton: Button
    // Keeps track of timer
    private var isRunning: Boolean = false
    // A globally available milliseconds until timer finishes
    private var globalTimeLeft: Long = 0
    // Default count down timer
    private val DEFAULT_TIME: Long = 30000

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Add timer button
        addTimerButton = findViewById(R.id.add_timer)
        addTimerButton.setOnClickListener{
           addTimer()
        }

        // Reset button
        resetButton = findViewById<Button>(R.id.reset_timer)
        resetButton.setOnClickListener{
            resetTimer()
        }

        // Progress bar
        progressIndicator = findViewById<CircularProgressIndicator>(R.id.progressIndicator)

        // Time text view
        timerTextView = findViewById(R.id.timer_textView)

        // Floating action button
        floatingButton = findViewById<ExtendedFloatingActionButton>(R.id.fab)
        floatingButton.setOnClickListener{
            if (isRunning){
                stopTimer()
            }else{
                if(globalTimeLeft > 0){
                    startTimer(globalTimeLeft)
                }else{
                    startTimer(DEFAULT_TIME)
                }
            }
        }
    }


    private fun startTimer(milli: Long) {
        countDownTimer = object : CountDownTimer(milli, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 60000
                val seconds = millisUntilFinished / 1000

                val timeLeft = "00:0${minutes}:${seconds}"
                timerTextView.text = timeLeft

                progressIndicator.progress = seconds.toInt()
                globalTimeLeft = millisUntilFinished
            }

            override fun onFinish() {
                resetTimer()
            }
        }.start()
        resumeTimer()
    }

    private fun resetTimer(){
        timerTextView.text = resources.getString(R.string.empty_timer_text)
        extendFab(true)
        enableResetButton(false)
        enableAddTimerButton(true)
        progressIndicator.visibility = View.GONE
        cancelTimer()
    }

    private fun cancelTimer(){
        if (this::countDownTimer.isInitialized){
            countDownTimer.cancel()
        }
        isRunning = false
        globalTimeLeft = 0
    }


    private fun resumeTimer(){
        if(!isRunning){
            isRunning = true
            extendFab(false)
            enableAddTimerButton(false)
            enableResetButton(false)
            progressIndicator.visibility = View.VISIBLE
        }
    }


    private fun stopTimer(){
        if(isRunning){
            isRunning = false
            extendFab(true)
            enableResetButton(true)
            countDownTimer.cancel()
        }
    }

    private fun enableResetButton(state: Boolean){
        resetButton.isEnabled = state
    }

    private fun enableAddTimerButton(state: Boolean){
        addTimerButton.isEnabled = state
    }

    private fun addTimer(){
        timerTextView.text = resources.getString(R.string.default_timer_text)
        progressIndicator.visibility = View.VISIBLE
        progressIndicator.progress = 30
        enableResetButton(true)
    }

    private fun extendFab(extend: Boolean){
        if(extend){
            floatingButton.extend()
            floatingButton.setIconResource(R.drawable.ic_outline_play_arrow_24)
        }else{
            floatingButton.shrink()
            floatingButton.setIconResource(R.drawable.ic_outline_pause_24)
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }


    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }
}