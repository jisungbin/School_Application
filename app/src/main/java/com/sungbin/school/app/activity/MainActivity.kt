package com.sungbin.school.app.activity

import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.sungbin.school.app.R
import com.sungbin.school.app.fragment.fragment_food
import com.sungbin.school.app.fragment.fragment_plan
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager
    var next_day:ImageButton? = null
    var pre_day:ImageButton?= null
    var info:TextView?= null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragmentTransaction = fragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.navigation_food -> {
                fragmentTransaction.replace(R.id.page, fragment_food()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_plan -> {
                fragmentTransaction.replace(R.id.page, fragment_plan()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notice -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        info = findViewById(R.id.info)
        pre_day = findViewById(R.id.pre_day)
        next_day = findViewById(R.id.next_day)

        fragmentManager.beginTransaction().add(R.id.page, fragment_food()).commit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toolbar_title.typeface = resources.getFont(R.font.sunflower_bold)
            info!!.typeface = resources.getFont(R.font.sunflower_light)
        }

        info!!.text = getString(R.string.copyright)
        pre_day!!.visibility = View.INVISIBLE
        next_day!!.visibility = View.INVISIBLE

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
