package com.tangramfactory.smartgym


import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class Age14UnderTest {

    private fun checkBirthDayPatterns14Under(birth: String): Boolean {
        if (birth.isNullOrEmpty()){
            return false
        }

        if (!isBirthDayPattern(birth) ){
            return false
        }

        if (!isBirthDayPattern(birth)){
            return false
        }

        if (!is14Age(birth)){
            return false
        }

        return true
    }

    private fun isBirthDayPattern(string: String) : Boolean = string.length == 8 &&
            string.substring(4, 6).toInt()<=12 && string.substring(4, 6).toInt()>0 &&
            string.substring(7).toInt()<=31 && string.substring(7).toInt()>0


    private fun is14Age(string: String) : Boolean {
        val age = 13
        if (string.length != 8) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())>age) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==age &&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>string.substring(4, 6).toInt())) return false

        if ((SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toInt()-string.substring(0, 4).toInt())==age&&
            (SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time).toInt()==string.substring(4, 6).toInt()) &&
            (SimpleDateFormat("dd", Locale.getDefault()).format(Calendar.getInstance().time).toInt()>=string.substring(6).toInt())) return false

        return true
    }




    private lateinit var today :Calendar
    //20220617
    @Before
    fun 오늘(){
        today = Calendar.getInstance()
        today.add(Calendar.YEAR, -13)

    }
    @Test
    fun 생일_패턴_테스트_오늘(){
        val value = today
        val result = checkBirthDayPatterns14Under((SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(value.time).toString()))
        assertFalse(result)
    }


    @Test
    fun 생일_패턴_테스트_전날(){
        val value = today
        value.add(Calendar.DATE , -1)
        val result = checkBirthDayPatterns14Under((SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(value.time).toString()))
        assertFalse(result)
    }

    @Test
    fun 생일_패턴_테스트_다음날(){
        val value = today
        value.add(Calendar.DATE , 1)
        val result = checkBirthDayPatterns14Under((SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(value.time).toString()))
        assert(result)
    }


    @Test
    fun 생일_패턴_테스트_전달(){
        val value = today
        value.add(Calendar.MONTH , -1)
        val result = checkBirthDayPatterns14Under((SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(value.time).toString()))
        assertFalse(result)
    }



    @Test
    fun 생일_패턴_테스트_다음달(){
        val value = today
        value.add(Calendar.MONTH , 1)
        val result = checkBirthDayPatterns14Under((SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(value.time).toString()))
        assert(result)
    }



}