package com.vmstechs.hpqrscanner.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateFormatterUtil {
    val IND = Locale("en", "IN")

    const val DATE_FORMAT_ONE = "yyyy.MM.dd G 'at' HH:mm:ss z" //2001.07.04 AD at 12:08:56 PDT

    const val DATE_FORMAT_TWO = "EEE, MMM d, ''yy" //Wed, Jul 4, '01

    const val DATE_FORMAT_THREE = "yyyyy.MMMM.dd GGG hh:mm aaa" //02001.July.04 AD 12:08 PM

    const val DATE_FORMAT_FOUR = "EEE, d MMM yyyy HH:mm:ss Z" //Wed, 4 Jul 2001 12:08:56 -0700

    const val EEE_MMM_dd_yyyy = "EEE MMM dd, yyyy" //Wed 4 Jul, 2001

    //public static String EEE_MMM_dd_yyyy = "EEE, MMM d, ''yy"; //Wed, Jul 4, '01
    const val yyyy_MM_dd = "yyyy-MM-dd"
    const val dd_MMM_yyyy_h_mm_a = "dd-MMM-yyyy h:mm a"
    const val dd_MM_yyyy_h_mm_a = "dd-MM-yyyy h:mm a"
    const val dd_MM_yyyy = "dd-MM-yyyy"
    const val EEE_MMM_d = "EEE, MMM d"
    const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    const val MMM_d = "MMM d"
    const val HH_mm_ss = "HH:mm:ss"
    const val h_mm_AM_PM = "h:mm a"
    const val hh_mm_AM_PM = "hh:mm a"
    const val MMM = "MMM"
    const val dd = "dd"
    const val EEEE = "EEEE"
    const val YYYY = "yyyy"
    const val MMM_YYYY = "MMM YYYY"

    fun parseDate(inputPattern: String, outputPattern: String, dateStr: String): String {
        val inputFormat  = SimpleDateFormat(inputPattern, IND)
        val outputFormat = SimpleDateFormat(outputPattern, IND)
        lateinit var strDate: String
        try {
            val date = inputFormat.parse(dateStr)!!
            strDate = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return strDate
    }

    fun parseDate(outputPattern: String, date: Date): String {
        val outputFormat = SimpleDateFormat(outputPattern, IND)
        lateinit var strDate: String
        try {
            strDate = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return strDate
    }


    fun getDifferenceOfTwoDates(dateFrom: String?, dateTo: String?): Long {
        val inputFormat = SimpleDateFormat(
            yyyy_MM_dd,
            IND
        )
        try {
            return TimeUnit.DAYS.convert(
                dateTo?.let { inputFormat.parse(it) }!!.time - dateFrom?.let { inputFormat.parse(it) }!!.time,
                TimeUnit.MILLISECONDS
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun isDate1BeforeToday(dateFormatPattern: String?, date1: String?): Boolean {
        val sdf = SimpleDateFormat(dateFormatPattern, IND)
        val today = SimpleDateFormat(
            yyyy_MM_dd,
            Locale.getDefault()
        ).format(
            Date()
        )
        try {
            val inDate = sdf.parse(date1!!)
            val todayDate = sdf.parse(today)
            assert(inDate != null)
            // return inDate.before(todayDate);
            return inDate == todayDate || inDate!!.before(todayDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun areTwoDatesEqual(date1: String?, date2: String?): Boolean {
        val sdf = SimpleDateFormat(yyyy_MM_dd, IND)
        try {
            val dateOne = sdf.parse(date1)
            val dateTwo = sdf.parse(date2)
            return dateOne == dateTwo
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }
}