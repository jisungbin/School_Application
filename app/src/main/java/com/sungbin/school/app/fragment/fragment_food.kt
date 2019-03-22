package com.sungbin.school.app.fragment

import android.Manifest
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.pedant.SweetAlert.SweetAlertDialog
import com.sungbin.school.app.school.School
import com.sungbin.school.app.school.SchoolMenu
import kotlinx.android.synthetic.main.navigation_meal.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.sungbin.school.app.utils.Utils
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.sungbin.school.app.R
import com.sungbin.school.app.listener.OnSwipeTouchListener


class fragment_food : Fragment() {

    var mealLoadDay:Int? = null
    var mealLoadYear:Int? = null
    var mealLoadMonth:Int? = null

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() { //수락
           Utils.toast(context, "이제 급식 정보가 저장됩니다.")
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) { //거절
            Utils.toast(context, "급식 정보를 저장하기 위해서 내부메모리에 접근 권한이 필요합니다.\n어플 설정애서 해당 권한의 사용을 허락해 주세요")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navigation_meal, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var dateSetListener:DatePickerDialog.OnDateSetListener? = null
        dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            date.text = year.toString() + "-" + (month + 1).toString() +"-" + day.toString()
            mealLoadMonth = month + 1
            mealLoadDay = day
            mealLoadYear = year
            MealTask().execute()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date.typeface = resources.getFont(R.font.sunflower_light)
            meal.typeface = resources.getFont(R.font.sunflower_medium)
        }

        mealLoadMonth = getTime("MM").toInt()
        mealLoadDay = getTime("dd").toInt()
        mealLoadYear = getTime("yyyy").toInt()

        date.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

        MealTask().execute()

        date.setOnClickListener {
            val year = date.text.split("-")[0].toString().toInt()
            val month = date.text.split("-")[1].toString().toInt() - 1
            val day = date.text.split("-")[2].toString().toInt()

            val dialog = DatePickerDialog(context!!, dateSetListener, year, month, day)
            dialog.window.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
        }

        next_day.setOnClickListener { //날짜 앞으로 가기
            var year = date.text.split("-")[0].toString().toInt()
            var month = date.text.split("-")[1].toString().toInt()
            var day = date.text.split("-")[2].toString().toInt()
            var lastday = (AllDay(year, month + 1, day) - AllDay(year, month, day)).toInt()

            if(day == lastday){ //막날에 다음날 눌렀을때
                if(month == 12){
                    year = year + 1
                    month = 1
                    day = 1
                }
                else{
                    month = month + 1
                    day = 1
                }
            }
            else{
                day = day + 1
            }

            mealLoadYear = year
            mealLoadMonth = month
            mealLoadDay = day

            date.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

            MealTask().execute()
        }

        pre_day.setOnClickListener { //날짜 뒤로가기
            var year = date.text.split("-")[0].toString().toInt()
            var month = date.text.split("-")[1].toString().toInt()
            var day = date.text.split("-")[2].toString().toInt()

            if(day == 1){ //1일에 뒤로가기 -> 전달 막날로
                if(month == 1){
                    month = 12
                    year = year - 1
                }
                else{
                    month = month - 1
                }

                day = 1 //임시값

                var lastday = (AllDay(year, month + 1, day) - AllDay(year, month, day)).toInt()

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = lastday

                date.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            }
            else{ //1일이 아닌 그 다음 날 부터 뒤로가기
                day = day - 1

                mealLoadYear = year
                mealLoadMonth = month
                mealLoadDay = day

                date.text ="$mealLoadYear-$mealLoadMonth-$mealLoadDay"

                MealTask().execute()
            }
        }

        meal.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeLeftToRight() {
                pre_day.performClick()
            }

            override fun onSwipeRightToLeft() {
                next_day.performClick()
            }
        })

    }

    fun getTime(type:String): String{
        val sdf = SimpleDateFormat(type)
        val time = sdf.format(Date(System.currentTimeMillis()))
        return time
    }

    private inner class MealTask : AsyncTask<Void?, Void?, Void?>() {

        val permissionCheck = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var textMenu:String? = null
        var menu: List<SchoolMenu>? = null
        var dialog: SweetAlertDialog? = null
        var content:String = "";
        var isText:Boolean = false

        override fun onPreExecute() {
            dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            dialog!!.progressHelper.barColor = Color.parseColor("#64b5f6")
            dialog!!.titleText = "\n\n급식 불러오는 중..."
            dialog!!.setCancelable(false)
            if(permissionCheck == PackageManager.PERMISSION_DENIED
                || (permissionCheck == PackageManager.PERMISSION_GRANTED &&
                        Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "null").equals("null"))) {
                 dialog!!.show()
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            if(permissionCheck == PackageManager.PERMISSION_GRANTED
                && !Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "null").equals("null")) {
                textMenu = Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "null")
                isText = true
                return null
            }
            else {
                menu = School(School.Type.HIGH, School.Region.CHUNGNAM, "N100000176")
                    .getMonthlyMenu(mealLoadYear!!, mealLoadMonth!!)
                return null
            }
        }

        override fun onPostExecute(result: Void?) {
            if(!isText) {
                for (i in 0 until menu!!.size) {
                    var str: String = "<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + (i+1).toString() + ">" + menu!![i].toString()
                    if(!str.contains("중식")) str =  "<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + (i+1).toString() + ">" + "\n\n" + "해당 일에는 급식이 없거나,\n학교에서 나이스에 급식을 업로드 하지 않았습니다."
                    content += "\n\n" + str.replaceFirst("\n", "")
                }
            }
            else content = textMenu!!

            content.replaceFirst("\n\n", "")

            var show_content = content.split("<" + mealLoadYear.toString() + "-" + mealLoadMonth.toString() + "-" + mealLoadDay.toString() + ">")[1]

            if(show_content.contains("중식")){
                if(getCharNumber(show_content, "<") > 1){
                    show_content = show_content.split("<")[0]
                }
            }
            else show_content = show_content.split(".")[0]

            meal.text = show_content

            if(permissionCheck == PackageManager.PERMISSION_DENIED
                || (permissionCheck == PackageManager.PERMISSION_GRANTED &&
                        Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "null").equals("null"))) {
                dialog!!.cancel()
            }

            when(permissionCheck) {
                PackageManager.PERMISSION_GRANTED ->{ //권한 수락
                    if(Utils.read("Meal Data/$mealLoadYear-$mealLoadMonth.data", "null").equals("null")) {
                        Utils.createFolder("Meal Data");
                        Utils.save("Meal Data/$mealLoadYear-$mealLoadMonth.data", content)
                        Utils.toast(context, "급식 정보가 저장되었습니다.");
                    }
                }
                else ->{ //권한 없음
                    TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setRationaleTitle("권한 필요")
                        .setRationaleMessage("급식 정보를 저장하기 위해서 내부메모리에 접근 권한이 필요합니다.\n권한 사용을 허용해 주세요.")
                        .setDeniedMessage("권한 사용에 거부하셔서 급식을 저장하지 못해 매번 서버에서 불러와야 됩니다.\n[설정] > [애플리케이션] > [2-3] > [권한] 에서 권한을 켜 주세오.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                }
            }

        }

    };

    fun AllDay(y:Int, m:Int, d:Int):Double{
        var month = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (y % 4 == 0) {
            month = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }
        var amoon = 0
        for(i in 1 until m){
            amoon += month.get(i-1)
        }
        var result = (Math.floor(365.24253716252537 * y) + amoon + d) - 366;
        return result
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