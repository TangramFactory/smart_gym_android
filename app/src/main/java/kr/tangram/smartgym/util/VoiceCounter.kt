package kr.tangram.smartgym.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import kr.tangram.smartgym.R
import java.util.*

class VoiceCounter//
(c: Context) : TextToSpeech.OnInitListener {

    private val tag = "VoiceCounter"
    private var context: Context = c
    private lateinit var tts: TextToSpeech
    private var ready:Boolean = false

    private var utteranceId:String? = null
    private var onCompleteFunction: ((utteranceId:String) -> Unit)? = null

    // 준비가 되는데 시간이 걸린다 //
    override fun onInit(status: Int) {
        log(tag,"init..")
        if (status != TextToSpeech.ERROR) {
            //
            tts.setOnUtteranceProgressListener(voiceProgressListener)
            //
            if (Build.VERSION.SDK_INT >= 24) {
                setLanguage24()
            } else {
                setLangauge21()
            }
            //
            ready = true
        }
    }

    //
    var voiceProgressListener = object : UtteranceProgressListener() {
        override fun onDone(utteranceId: String?) {
            if(utteranceId!=null) {
                if(onCompleteFunction!=null) onCompleteFunction?.let { it(utteranceId) }
            }
        }
        override fun onError(utteranceId: String?) {
        }
        override fun onStart(utteranceId: String?) {
        }
    }

    //
    fun create(context:Context) {

        //
        if(ready==true)terminate()

        //
        this.context = context
        tts = TextToSpeech(context,this)

        //
        utteranceId = this.hashCode().toString()
    }

    //
    fun terminate() {
        try {
            tts.stop()
            tts.shutdown()
        } catch (e:Exception) {}
        ready = false
    }

    //
    lateinit var runnable:Runnable
    var voiceString:String = ""
    fun speak(str:String) {
        log(tag,str)

        // TextToSpeech Init 하고 OnInit 이벤트가 들어올 때까지 기다려야 한다
        // Runable에서 체크해서 래디가 되면 >> 그 때 말 한다
        voiceString = " $str"
        if(ready) {
            speakAction(voiceString)
        } else {
            runnable = Runnable {
                runJob()
            }
            Handler().postDelayed(runnable,100)
        }
    }

    //
    fun count(c:Int) {

        //
        this.utteranceId = null
        this.onCompleteFunction = null

        // TTS에서 지원 하는 언어는 몇개 ?
        var ttsLocation = tts.availableLanguages
        var locCount = try { ttsLocation.count() } catch (e:Exception) {0}

        //
        if(locCount==0) {

            //***** 지원하는 언어가 없는 경우

//            // 지원되는 언어가 없다 >> 영어로 ?? 영어는 되느가 ~?
//            tts.language = Locale.ENGLISH
//            speak(c.toString())

        } else {

            // 시스템에 설정된 언어중에서 TTS지원되는 언어 찾아 >>
            val sysLocation = getEnableTTSLocation(ttsLocation)
            log(tag,"tts lang " + tts.language )

            if(sysLocation==null) {

                // 지원되는 언어가 아니면 그냥 숫자로 말해 >> 지원되는 언어 아무개나 나오겠지
                tts.language = ttsLocation.elementAt(0)
                speak( c.toString() )

            } else {

                // 언어 지원되는 경우에는 'TH' 지역별로 챙겨서 말해
                tts.language = sysLocation
                speak(c.toString() + context.resources.getString(R.string.word_count_ordinal))
            }
        }
    }

    //
    fun countComplete(c:Int,uId:String,func:((utteranceId:String) -> Unit)?) {

        // 카운트 겟수가 1,2개 일 때는 말하지 않고 바로 끝낸다
        if(c<3) {
            if (func != null) func(uId)
            return
            // TTS가 준비되지 않은 상태에서 말하게 되는 경우가 있다.
        }

        //
        this.utteranceId = uId
        this.onCompleteFunction = func

        // TTS에서 지원 하는 언어는 몇개 ?
        val ttsLocation = tts.availableLanguages
        val locCount = try { ttsLocation.count() } catch (e:Exception) {0}

        //
        if(locCount==0) {

            //***** 지원하는 언어가 없는 경우
//            // 지원되는 언어가 없다 >> 영어로 ?? 영어는 되느가 ~?
//            tts.language = Locale.ENGLISH
//            speak(c.toString())

        } else {

            // 시스템에 설정된 언어중에서 TTS지원되는 언어 찾아 >>
            val sysLocation = getEnableTTSLocation(ttsLocation)
            log(tag,"tts lang " + tts.language )

            if(sysLocation==null) {

                // 지원되는 언어가 아니면 그냥 숫자로 말해 >> 지원되는 언어 아무개나 나오겠지
                tts.language = ttsLocation.elementAt(0)
                speak( c.toString() )

            } else {

                // 언어 지원되는 경우에는 'TH' 지역별로 챙겨서 말해
                tts.language = sysLocation

                //
                var cString = c.toString() + context.resources.getString(R.string.word_count_ordinal)
                cString = String.format( context.resources.getString(R.string.basiccount_pop_voice_complete), cString )
                speak(cString)
            }
        }
    }


    //
    private fun getEnableTTSLocation(ttsLocations:Set<Locale>):Locale? {

        val defaultLocale = Locale.ENGLISH

        log(tag," tts langs .. " + ttsLocations.toString() )

        // 시스템에 설정된 언어는 무엇인가 ?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            var currentLocales = context.resources.configuration.locales
            for ( i in 0 .. currentLocales.size()-1 ) {

                var sysLocale = currentLocales.get(i)
                for (location in ttsLocations) {

                    log(tag, location.isO3Language + "/" + sysLocale.isO3Language  )
                    if(location.isO3Language==sysLocale.isO3Language) {
                        /// 같은 언어가 있으면 내보낸다
                        log(tag, "same .. " + location.isO3Language + "=" + sysLocale.isO3Language )
                        return location
                    }
                }

            }

        } else {

            //
            var locale = context.resources.configuration.locale
            for (location in ttsLocations) {
                if (location.isO3Language == locale.isO3Language) {
                    return location
                }
            }

        }

        //
        return null
    }

    //
    fun runJob() {
        log(tag,"run job ")
        if(ready) {
            speakAction(voiceString)
        } else {
            Handler().postDelayed(runnable,100)
        }
    }

    //
    fun speakAction(str:String){
        //
        if (Build.VERSION.SDK_INT >= 21) {
            ttsGreater21(str)
        } else {
            ttsUnder20(str)
        }
    }

    //
    private fun getPrefVolume():Float {
        return AppSetting.sound_jump * 100f
    }

    //
    private fun ttsUnder20(text:String) {
        log(tag, "ttsUnder20 " + text)

        //
        val map = HashMap<String, String>()
        var id:String = utteranceId ?: this.hashCode().toString()
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id)
        map.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, getPrefVolume().toString() )
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    //
    private fun ttsGreater21(text: String) {
        log(tag, "ttsGreater21 " + text)

        //
        var params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, getPrefVolume() )
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }


    // 일단 지원되는 언어 아무거나 설정
    //
    private fun setLangauge21() {
        log(tag, "setLangauge21")
        val currentLocal = context.resources.configuration.locale
        tts.language = currentLocal
    }

    //
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLanguage24() {
        log(tag, "setLanguage24")
        val currentLocal = context.resources.configuration.locales
        tts.language = currentLocal.get(0)
    }

    init {
        utteranceId = this.hashCode().toString()
    }

}


//        //
////        var spannableString = SpannableString("")
////
////        var spanCar = TtsSpan.CardinalBuilder(10).build()
////        var spanOrd = TtsSpan.OrdinalBuilder().setNumber(20).build()
//
////        SpannableStringBuilder().append(spannableString,spanCar,0)
////        SpannableStringBuilder().append(spannableString,spanOrd,3)
//
////        spannableString.setSpan(spanCar, 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
////        spannableString.setSpan(spanOrd, 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//
//
////        val ttsSpans = arrayOfNulls<TtsSpan>(3)
////        ttsSpans[0] = TtsSpan.CardinalBuilder("1234").build()
////        ttsSpans[1] = TtsSpan.OrdinalBuilder().setNumber(1234).build()
////        ttsSpans[2] = TtsSpan.DigitsBuilder().setDigits("1234").build()
//
//
////        val ttsSpans = SpannableString("10 20")
//
////        ttsSpans.
//
//
//
////        val lParams = Bundle()
////        lParams.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, Constants.TTS_AUDIO_STREAM)
//
//
//        val text = "안녕하세요 ~ "
//        val digits = " 1020개 뛰었습니다."
//        val finalString = text + digits
//        val lSpannedMsg = SpannableString(finalString)
//        val lSpan = TtsSpan.OrdinalBuilder().setNumber(digits).build()
//        val lSpanText = TtsSpan.TextBuilder(text).build()
//
//        lSpannedMsg.setSpan(lSpan, finalString.indexOf(digits), finalString.indexOf(digits) + digits.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        lSpannedMsg.setSpan(lSpanText, finalString.indexOf(text), finalString.indexOf(text) + text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//
//
//        log(tag, "--------" + lSpannedMsg )
//
//        tts.speak( lSpannedMsg , TextToSpeech.QUEUE_FLUSH, null, utteranceId)
