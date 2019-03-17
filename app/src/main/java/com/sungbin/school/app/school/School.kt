package com.sungbin.school.app.school

import android.os.StrictMode
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.*

class School
/**
 * 불러올 학교 정보를 설정합니다.
 *
 * @param type   교육기관의 종류입니다. (School.Type 에서 병설유치원, 초등학교, 중학교, 고등학교 중 선택)
 * @param region 관할 교육청의 위치입니다. (School.Region 에서 선택)
 * @param code   교육기관의 고유 코드입니다.
 */
    (
    /**
     * 교육기관의 종류
     */
    var type: Type,
    /**
     * 교육기관 관할 지역
     */
    var region: Region,
    /**
     * 교육기관 고유 코드
     */
    var code: String
) {

    /**
     * 캐시용 해시맵
     */
    private val monthlyMenuCache: MutableMap<Int, List<SchoolMenu>>
    private val monthlyScheduleCache: MutableMap<Int, List<SchoolSchedule>>

    /**
     * 불러올 교육청 소속 교육기관의 종류
     */
    enum class Type private constructor(val id: String) {
        /* 병설유치원 */ KINDERGARTEN("1"),
        /* 초등학교 */ ELEMENTARY("2"),
        /* 중학교 */ MIDDLE("3"),
        /* 고등학교 */ HIGH("4")
    }

    /**
     * 불러올 교육기관의 관할 지역 (교육청)
     */
    enum class Region private constructor(val url: String) {

        /* 서울 */ SEOUL("sen.go.kr"),
        /* 인천 */ INCHEON("ice.go.kr"),
        /* 부산 */ BUSAN("pen.go.kr"),
        /* 광주 */ GWANGJU("gen.go.kr"),
        /* 대전 */ DAEJEON("dje.go.kr"),
        /* 대구 */ DAEGU("dge.go.kr"),
        /* 세종 */ SEJONG("sje.go.kr"),
        /* 울산 */ ULSAN("use.go.kr"),
        /* 경기 */ GYEONGGI("goe.go.kr"),
        /* 강원 */ KANGWON("kwe.go.kr"),
        /* 충북 */ CHUNGBUK("cbe.go.kr"),
        /* 충남 */ CHUNGNAM("cne.go.kr"),
        /* 경북 */ GYEONGBUK("gbe.go.kr"),
        /* 경남 */ GYEONGNAM("gne.go.kr"),
        /* 전북 */ JEONBUK("jbe.go.kr"),
        /* 전남 */ JEONNAM("jne.go.kr"),
        /* 제주 */ JEJU("jje.go.kr")
    }

    init {
        this.monthlyMenuCache = HashMap<Int, List<SchoolMenu>>()
        this.monthlyScheduleCache = HashMap<Int, List<SchoolSchedule>>()
    }

    /**
     * 월간 급식 메뉴를 불러옵니다.
     *
     * @param year  해당 년도를 yyyy 형식으로 입력. (ex. 2016)
     * @param month 해당 월을 m 형식으로 입력. (ex. 3, 12)
     * @return 각 일자별 급식메뉴 리스트
     */
    @Throws(SchoolException::class)
    fun getMonthlyMenu(year: Int, month: Int): List<SchoolMenu>? {

        val cacheKey = year * 12 + month

        if (this.monthlyMenuCache.containsKey(cacheKey))
            return this.monthlyMenuCache[cacheKey]

        val targetUrl = StringBuilder()

        targetUrl.append("https://stu.").append(region.url).append("/").append(MONTHLY_MENU_URL)
        targetUrl.append("?schulCode=").append(code)
        targetUrl.append("&schulCrseScCode=").append(type.id)
        targetUrl.append("&schulKndScCode=0").append(type.id)
        targetUrl.append("&schYm=").append(year).append(String.format("%02d", month))
        targetUrl.append("&")

        try {
            var content = getContentFromUrl(URL(targetUrl.toString()))
            content = Utils.before(Utils.after(content, "<tbody>"), "</tbody>")

            // 리턴하기 전 캐시에 데이터를 저장합니다.
            val monthlyMenu = SchoolMenuParser.parse(content)
            this.monthlyMenuCache[cacheKey] = monthlyMenu

            return monthlyMenu

        } catch (e: MalformedURLException) {
            throw SchoolException("교육청 접속 주소가 올바르지 않습니다.")
        }

    }

    /**
     * 월간 학사 일정을 불러옵니다.
     *
     * @param year  해당 년도를 yyyy 형식으로 입력. (ex. 2016)
     * @param month 해당 월을 m 형식으로 입력. (ex. 3, 12)
     * @return 각 일자별 학사일정 리스트
     */
    @Throws(SchoolException::class)
    fun getMonthlySchedule(year: Int, month: Int): List<SchoolSchedule>? {

        val cacheKey = year * 12 + month

        if (this.monthlyScheduleCache.containsKey(cacheKey))
            return this.monthlyScheduleCache[cacheKey]

        val targetUrl = StringBuilder()

        targetUrl.append("https://stu.").append(region.url).append("/").append(SCHEDULE_URL)
        targetUrl.append("?schulCode=").append(code)
        targetUrl.append("&schulCrseScCode=").append(type.id)
        targetUrl.append("&schulKndScCode=0").append(type.id)
        targetUrl.append("&ay=").append(year)
        targetUrl.append("&mm=").append(String.format("%02d", month))
        targetUrl.append("&")

        try {
            var content = getContentFromUrl(URL(targetUrl.toString()))
            content = Utils.before(Utils.after(content, "<tbody>"), "</tbody>")

            val monthlySchedule = SchoolScheduleParser.parse(content)
            this.monthlyScheduleCache[cacheKey] = monthlySchedule

            return monthlySchedule

        } catch (e: MalformedURLException) {
            throw SchoolException("교육청 접속 주소가 올바르지 않습니다.")
        }

    }

    fun clearCache() {
        this.monthlyScheduleCache.clear()
        this.monthlyMenuCache.clear()
    }

    companion object {

        private val MONTHLY_MENU_URL = "sts_sci_md00_001.do"
        private val SCHEDULE_URL = "sts_sci_sf01_001.do"
        private val SCHOOL_CODE_URL = "spr_ccm_cm01_100.do"

        @Throws(SchoolException::class)
        private fun getContentFromUrl(url: URL): String {
            try {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitNetwork().build())

                val reader = BufferedReader(InputStreamReader(url.openStream()))

                val buffer = StringBuilder()


                while (true) {
                    val inputLine = reader.readLine() ?: break
                    buffer.append(inputLine)
                }

                reader.close()
                return buffer.toString()

            } catch (e: IOException) {
                throw SchoolException("교육청 서버에 접속하지 못하였습니다.")
            }

        }

        @Throws(SchoolException::class)
        fun find(region: Region, name: String): School {
            try {
                val targetUrl = StringBuilder()

                targetUrl.append("https://par.").append(region.url).append("/").append(SCHOOL_CODE_URL)
                targetUrl.append("?kraOrgNm=").append(URLEncoder.encode(name, "utf-8"))
                targetUrl.append("&")

                // 원본 데이터는 JSON형식으로 이루어져 있습니다.
                var content = getContentFromUrl(URL(targetUrl.toString()))
                content = Utils.before(Utils.after(content, "orgCode"), "schulCrseScCodeNm")

                // 기관 종류와 코드를 구합니다.
                val schoolCode = content.substring(3, 13)
                val schoolType = Utils.before(Utils.after(content, "schulCrseScCode\":\""), "\"")

                return School(Type.values()[Integer.parseInt(schoolType) - 1], region, schoolCode)

            } catch (e: Exception) {
                e.printStackTrace()
                throw SchoolException("학교를 찾을 수 없습니다.")
            }

        }
    }
}