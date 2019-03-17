package com.sungbin.school.app.activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.sungbin.school.app.R
import kotlinx.android.synthetic.main.splash_activity.*


class splash_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sungbin.school.app.R.layout.splash_activity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            main_title.typeface = resources.getFont(R.font.sunflower_bold)
            copyright.typeface = resources.getFont(R.font.sunflower_light)
        }

        Handler().postDelayed({
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }, 2000)
    }

    override fun onBackPressed() {
    }
}
