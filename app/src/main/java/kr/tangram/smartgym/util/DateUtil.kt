package kr.tangram.smartgym.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getNewDateFormat(dateTime:String, newFormat:String, oldFormat:String) : String {
    try{
        val cal = Calendar.getInstance()
        cal.timeInMillis = SimpleDateFormat(oldFormat).parse(dateTime).time
        val sdf = SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(),newFormat))
        return sdf.format(cal.time)

//        return SimpleDateFormat(newFormat).format(SimpleDateFormat(oldFormat).parse(dateTime)).toString()
    }catch (e:Exception){
        return ""
    }
}

fun getNowDateFormat(format:String) : String{
    try{
        return  SimpleDateFormat(format).format(Date()).toString()
    }catch (e:Exception){
        return ""
    }
}

fun getPeriodDate(dateTime:String, format: String, term: Int) : String{
    var cal = Calendar.getInstance()
    cal.time = SimpleDateFormat(format).parse(dateTime)
    cal.add(Calendar.DATE, term)

    return SimpleDateFormat(format).format(cal.time)
}
