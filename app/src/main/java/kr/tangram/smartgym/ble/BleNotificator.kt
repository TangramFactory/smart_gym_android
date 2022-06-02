/*
 * Copyright (c) 2020 TANGRAM FACTORY, INC. All rights reserved.
 * @auth kang@tangram.kr
 * @date 10/6/20 9:51 AM.
 */

package kr.tangram.smartgym.ble

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.util.SoundEffect
import kr.tangram.smartgym.util.bleConnection


class BleNotificator {

	//
	val tag = "BleNotificater"

	lateinit var CHANNEL_MAIN: mChannel
	lateinit var CONTEXT: Context


	//
	fun init(context : Context) {
		//
		this.CONTEXT = context

		// 노티 채널 설정
		CHANNEL_MAIN = mChannel()
		CHANNEL_MAIN.init(
				CONTEXT.getString(R.string.notification_bleautoclose_channel_num).toInt(),
				CONTEXT.getString(R.string.notification_bleautoclose_channel_id),
				CONTEXT.getString(R.string.notification_bleautoclose_channel_title),
				CONTEXT.getString(R.string.notification_bleautoclose_channel_description)
		)

		// 채널 등록하기
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val importance = NotificationManager.IMPORTANCE_HIGH //IMPORTANCE_DEFAULT

			val channelPowersave = NotificationChannel(CHANNEL_MAIN.id, CHANNEL_MAIN.title, importance)
			channelPowersave.description = CHANNEL_MAIN.description

//			val soundUri = Uri.parse(
//					"android.resource://" +
//							getApplicationContext().packageName +
//							"/" +
//							R.raw.disconnect)
//			val audioAttributes = AudioAttributes.Builder()
//					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//					.setUsage(AudioAttributes.USAGE_ALARM)
//					.build()
//			channelPowersave.setSound(soundUri,audioAttributes)

			val notificationManager = CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channelPowersave)

		}
	}

	//
	fun closeAction(context : Context) {

		//
		init(context)

		// --
		val intent = Intent(CONTEXT, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
		CONTEXT.startActivity(intent) //--- 미리 가서 기다린다 하고 싶은데 안간다

		// 끊김 사운드 재생
		SoundEffect.play.disconnect(1.0f)

		// --
//		val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
		// --->> 눌러서 앱을 실행하는 경우 알림을 받았을 때 누르면 자동으로 또 접속된다. 이건 아니지..
        val notificationBuilder = NotificationCompat.Builder(CONTEXT, CHANNEL_MAIN.id).apply {
            setSmallIcon(R.drawable.ic_notification_icon_smartrope)
            setContentTitle(CONTEXT.getString(R.string.notification_bleclose_title))
            setContentText(CONTEXT.getString(R.string.notification_bleclose_message))
	        setStyle(NotificationCompat.BigTextStyle().bigText(CONTEXT.getString(R.string.notification_bleclose_message))) //--
//			setContentIntent(pendingIntent)
	        setNotificationSilent() //-- 무음
	        setOnlyAlertOnce(true)
	        setAutoCancel(true)

        }
        val notificationManager: NotificationManager = CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(CHANNEL_MAIN.num, notificationBuilder.build())

        // 접속을 끊는다 //
		bleConnection.AUTOCONNECT = false // 다시 자동으로 연결되는 경우를 막는다. >> 앱을 다시 열면 살린다.
		bleConnection.disconnect()

	}


	//
	class mChannel {
		var num = 0
		var id = "channel_id"
		var title = "name"
		var description = "description"
		fun init(num : Int, id : String, title : String, description : String) {
			this.num = num
			this.id = id
			this.title = title
			this.description = description
		}
	}
}