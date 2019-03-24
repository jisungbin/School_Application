package com.sungbin.school.app.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


object Utils{

    var sdcard = Environment.getExternalStorageDirectory().absolutePath

    fun endcodeBase64(text:String): String{
        return android.util.Base64.encodeToString(text.toByteArray(), 0)
    }

    fun decodeBase64(text:String): String{
        try {
            return android.util.Base64.decode(text, 0).toString()
        }
        catch (e:Exception){
            Log.d("ERROR", e.toString())
            return ""
        }
    }

    fun createFolder(name:String){
        File("$sdcard/2학년 3반/$name/").mkdirs()
    }

    fun toast(ctx: Context?, content:String){
        Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show()
    }

    fun read(name:String, _null:String): String{
        try {
            val file = File("$sdcard/2학년 3반/$name/")
            if (!file.exists()) return _null
            val fis = FileInputStream(file)
            val isr = InputStreamReader(fis)
            val br = BufferedReader(isr)
            var str = br.readLine()

            while(true) {
                val inputLine = br.readLine() ?: break
                str += "\n" + inputLine
            }
            fis.close()
            isr.close()
            br.close()
            return str.toString()
        }
        catch (e: Exception) {
            Log.e("READ", e.toString())
        }

        return _null
    }

    fun save(name:String, content:String){
        try {
            val file = File("$sdcard/2학년 3반/$name")
            val fos = java.io.FileOutputStream(file)
            fos.write(content.toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("SAVE", e.toString())
        }

    }

    fun delete(name:String){
        File("$sdcard/2학년 3반/$name").delete()
    }



}