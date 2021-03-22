package com.sdirin.games.testingsnake.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sdirin.games.testingsnake.R

class SplashActivity : AppCompatActivity() {
    var _active = true
    var _splashTime = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = resources.getColor(R.color.colorAccent)//getColor(R.color.colorAccent)

        val splashTread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    while (_active && waited < _splashTime) {
                        Thread.sleep(100)
                        if (_active) {
                            waited += 100
                        }
                    }
                } catch (e: Exception) {

                } finally {

                    startActivity(Intent(this@SplashActivity,
                            MainActivity::class.java))
                    finish()
                }
            }
        }
        splashTread.start()

    }
}
