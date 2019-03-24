package com.sungbin.school.app.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.sungbin.school.app.utils.NotificationManager
import com.sungbin.school.app.utils.Utils

abstract class MealAlarmService : Service() {
    private var mealLoadYear = ""
    private var mealLoadMonth = ""
    private var mealLoadDay = ""

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mealLoadYear = intent.getStringExtra("mealLoadYear")
        mealLoadMonth = intent.getStringExtra("mealLoadMonth")
        mealLoadDay = intent.getStringExtra("mealLoadDay")

        showMealNotification()

        return Service.START_NOT_STICKY
    }

    fun showMealNotification(){
        NotificationManager.setGroupName("School Information")
        NotificationManager.createChannel(this, "Today Meal Alarm", "Today meal notification alarm service.")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val content = Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "파일 오류!")

            content.replaceFirst("\n\n", "")

            var show_content = content.split("<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + mealLoadDay.toString() + ">")[1]

            if(show_content.contains("중식")){
                if(getCharNumber(show_content, "<") > 1){
                    show_content = show_content.split("<")[0]
                }
            }
            else show_content = show_content.split(".")[0]

            show_content = show_content.split("\n\n[석식]")[0]
                .split("[중식]")[1]
            show_content = "[중식]" + show_content

            NotificationManager.showBigTextStyleNotification(this, 1, "오늘의 급식", "밑으로 내리면 오늘의 급식을 확인할 수 있습니다.", show_content)
        }
        else{
            NotificationManager.showNormalNotification(this, 1, "오늘의 급식", "저장된 급식 정보가 없어서 오늘의 급식을 표시할 수 없습니다.")
        }
    }

    fun getCharNumber(str: String, equ:String): Int {
        var count = 0
        for (i in 0 until str.length) {
            if (str[i].toString() == equ)
                count++
        }
        return count
    }
}