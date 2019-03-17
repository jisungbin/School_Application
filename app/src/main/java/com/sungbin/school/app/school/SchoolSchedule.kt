package com.sungbin.school.app.school

class SchoolSchedule {

    var schedule: String

    /**
     * 일정이 없을 경우
     */
    constructor() {
        schedule = ""
    }

    /**
     * 일정이 있을 경우
     *
     * @param schedule 학사일정 인스턴스
     */
    constructor(schedule: String) {
        this.schedule = schedule
    }

    override fun toString(): String {
        return schedule
    }
}