package com.sungbin.school.app.school

class SchoolMenu {
    /**
     * 조식
     */
    var breakfast: String

    /**
     * 중식
     */
    var lunch: String

    /**
     * 석식
     */
    var dinner: String

    init {
        dinner = "급식이 없습니다"
        lunch = dinner
        breakfast = lunch
    }

    override fun toString(): String {
        return "\n\n$breakfast\n\n$lunch\n\n$dinner"
    }
}