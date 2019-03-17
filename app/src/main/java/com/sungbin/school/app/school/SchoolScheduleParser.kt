package com.sungbin.school.app.school


import java.util.*
import java.util.regex.Pattern

/**
 * NEIS API
 * 전국 교육청 소속 교육기관의 학사일정, 메뉴를 간단히 불러올 수 있습니다.
 *
 * @author HyunJun Kim
 * @version 3.1
 */
internal object SchoolScheduleParser {

    private var schedulePattern: Pattern? = null

    /**
     * 웹에서 가져온 데이터를 바탕으로 학사일정을 파싱합니다.
     */
    @Throws(SchoolException::class)
    fun parse(rawData: String): List<SchoolSchedule> {
        var rawData = rawData

        if (schedulePattern == null) {
            schedulePattern = Pattern.compile("<strong></strong>")
        }

        if (rawData.length < 1)
            throw SchoolException("불러온 데이터가 올바르지 않습니다.")

        val monthlySchedule = ArrayList<SchoolSchedule>()

        /*
         파싱 편의를 위해 모든 공백을 제거합니다.
         일정 텍스트에는 공백이 들어가지 않으므로, 파싱 결과에는 영향을 주지 않습니다.
         */
        rawData = rawData.replace("\\s+".toRegex(), "")

        val chunk = rawData.split("textL\">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (i in 1 until chunk.size) {
                var trimmed = Utils.before(chunk[i], "</div>")
                val date = Utils.before(Utils.after(trimmed, ">"), "</em>")

                // 빈 공간은 파싱하지 않습니다.
                if (date.length < 1) continue

                // 일정을 가져옵니다.
                val schedule = StringBuilder()
                while (trimmed.contains("<strong>")) {
                    val name = Utils.before(Utils.after(trimmed, "<strong>"), "</strong>")
                    schedule.append(name)
                    schedule.append("\n")
                    trimmed = Utils.after(trimmed, "</strong>")
                }
                monthlySchedule.add(SchoolSchedule(schedule.toString()))
            }
            return monthlySchedule

        } catch (e: Exception) {
            throw SchoolException("학사일정 정보 파싱에 실패했습니다. API를 최신 버전으로 업데이트 해 주세요.")
        }

    }

}