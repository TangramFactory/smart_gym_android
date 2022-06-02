package kr.tangram.smartgym.util

import android.content.Context
import android.media.SoundPool
import kr.tangram.smartgym.R

class SoundEffect {

    companion object {

        private lateinit var soundPool:SoundPool
        private var soundArray = HashMap<String,Int>()

        //
        fun init(context: Context) {

            soundPool = SoundPool.Builder().setMaxStreams(10).build()

            //
            soundArray.set("connect",soundPool.load(context, R.raw.connect,1))
            soundArray.set("disconnect",soundPool.load(context,R.raw.disconnect,1))
            soundArray.set("pop", soundPool.load(context,R.raw.pop,1))
            soundArray.set("complete", soundPool.load(context,R.raw.complete,1))
            soundArray.set("question", soundPool.load(context,R.raw.question,1))
            soundArray.set("click", soundPool.load(context,R.raw.click,1))
            soundArray.set("ready", soundPool.load(context,R.raw.ready,1))
            soundArray.set("alert", soundPool.load(context,R.raw.alert,1))
            soundArray.set("open", soundPool.load(context,R.raw.open,1))
            soundArray.set("close", soundPool.load(context,R.raw.close,1))

        }

        //
        private fun play(sound_id:Int,volume: Float) {
            soundPool.play(sound_id,volume,volume,1,0,1.0f)
        }

    }

    //
    class play {
        companion object {
            private val tag = "play"
            fun connect(v:Float) {
                try {
                    play(soundArray["connect"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun disconnect(v:Float) {
                try {
                    play(soundArray["disconnect"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun pop(v:Float) {
                log(tag,"sound pop >  $v ")
                try {
                    play(soundArray["pop"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun complete(v:Float) {
                try {
                    play(soundArray["complete"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun question(v:Float) {
                try {
                    play(soundArray["question"]!!, v)
                } catch (e:NullPointerException) {}
            }
            fun click(v:Float) {
                try {
                    play(soundArray["click"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun ready(v:Float) {
                try {
                    play(soundArray["ready"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun alert(v:Float) {
                try {
                    play(soundArray["alert"]!!, v)
                } catch (e:NullPointerException) {}
            }
            fun open(v:Float) {
                try {
                    play(soundArray["open"]!!,v)
                } catch (e:NullPointerException) {}
            }
            fun close(v:Float) {
                try {
                    play(soundArray["close"]!!,v)
                } catch (e:NullPointerException) {}
            }
        }
    }


}