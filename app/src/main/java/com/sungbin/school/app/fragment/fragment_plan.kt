package com.sungbin.school.app.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sungbin.school.app.R
import com.sungbin.school.app.activity.MainActivity
import kotlinx.android.synthetic.main.navigation_plan.*


class fragment_plan : Fragment() {

    lateinit var next_day: ImageButton
    lateinit var pre_day: ImageButton
    lateinit var info: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navigation_plan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        info = (context as MainActivity).info!!
        pre_day = (context as MainActivity).pre_day!!
        next_day = (context as MainActivity).next_day!!

        info.text = getString(R.string.copyright)
        pre_day.visibility = View.INVISIBLE
        next_day.visibility = View.INVISIBLE

        Glide.with(this).load(R.drawable.plan).into(plan_image)
    }

}