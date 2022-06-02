package kr.tangram.smartgym.util

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BleSmartRopeState
import kr.tangram.smartgym.ui.intro.IntroActivity
import kr.tangram.smartgym.ui.main.MainActivity
import org.json.JSONException
import java.text.DecimalFormat

//
class Notificater_Channel {
    var num = 0
    var id = "channel_id"
    var title = "name"
    var description = "description"
    fun init(num:Int,id:String,title:String,description:String) {
        this.num = num
        this.id = id
        this.title = title
        this.description = description
    }
}

//
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //
        if(context==null) return

        //
        when(intent!!.action) {
            "Notification_Close"-> {

                // 노티피케이션 종료
                val notifManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notifManager.cancel(Notificater.CHANNEL_MAIN.num)

                // 엑티비티 종료
                try {
                    (Notificater.context as Activity).finishAndRemoveTask()
                } catch (e:NullPointerException) {

                }

                // 프로세스 종료
                System.runFinalization()
                System.exit(0)
            }

        }

    }
}


//
class Notificater {

    companion object {

        const val tag = "Notificater"

        lateinit var context:Context

        private lateinit var mBuilder:NotificationCompat.Builder
        private lateinit var remoteViews:RemoteViews
        private lateinit var notificationManager: NotificationManager
        private lateinit var serviceConnection:ServiceConnection

        lateinit var CHANNEL_MAIN:Notificater_Channel

        fun init(context:Context) {

            this.context = context

            // 노티 채널 설정
            CHANNEL_MAIN = Notificater_Channel()
            CHANNEL_MAIN.init(1, "notification_main","","스마트로프앱이 실행되고 있습니다.")

            // 안드로이드 오레오 이상에서는 채널이 꼭 필요
            createChannel()

            // 열고
            openWidget()

        }

        //
        fun openWidget() {

//            log(tag,"open")
//            log(tag,">>> NOTIFICATION " )

            //
            serviceConnection = object : ServiceConnection {

                override fun onServiceDisconnected(name: ComponentName?) {
                    //context.unbindService(serviceConnection)
                }

                @SuppressLint("WrongConstant")
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

                    //
                    try {
                        (service as KillNotificationService.KillBinder).service.startService(Intent(context, KillNotificationService::class.java))
                    } catch (e:IllegalStateException) {}

                    //
                    remoteViews = RemoteViews(context.packageName, R.layout.custom_notification_view)

                    //
                    remoteViews.setImageViewResource(R.id.img, R.drawable.ic_notification_icon_logo)
                    remoteViews.setImageViewResource(R.id.img_jump, R.drawable.ic_home_basiccount_jump)
                    remoteViews.setImageViewResource(R.id.img_calorie, R.drawable.ic_home_basiccount_calories)
                    remoteViews.setImageViewResource(R.id.img_time, R.drawable.ic_home_basiccount_duration)
                    remoteViews.setImageViewResource(R.id.img_goal, R.drawable.ic_home_basiccount_goal)
                    remoteViews.setImageViewResource(R.id.img_link, R.drawable.ic_notification_icon_unlink)
                    remoteViews.setImageViewResource(R.id.img_close, R.drawable.ic_notification_icon_close)
                    remoteViews.setOnClickPendingIntent(R.id.img_close, makeNotificationPending("Notification_Close"))

                    //
                    mBuilder = NotificationCompat.Builder(
                            context,
                            CHANNEL_MAIN.id
                    )
                            .setTicker("Ticker Text")
                            .setSmallIcon(R.drawable.ic_notification_icon_smartrope)
                            .setColor(ContextCompat.getColor(context,R.color.a01_accent)) // Application Color
                            .setContentTitle(CHANNEL_MAIN.title)
                            .setContentText(CHANNEL_MAIN.description)
                            .setVisibility(Notification.VISIBILITY_PRIVATE)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE) //**
//                            .addAction(R.drawable.com_facebook_button_icon,"Close",applicationClose)
//                            .setColorized(true)
                            .setOngoing(true)
                            .setAutoCancel(false)
                            .setContent(remoteViews)
                            .setSound(null)
//                            .setNumber(0)


                    // 누르면 홈엑티비티로 가도록 ..
                    val resultIntent = Intent( context , MainActivity::class.java)
                    resultIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

                    val stackBuilder = TaskStackBuilder.create(context)
                    stackBuilder.addParentStack(IntroActivity::class.java)
                    stackBuilder.addNextIntent(resultIntent)
                    val resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT)

                    mBuilder.setContentIntent(resultPendingIntent)

                    //
                    notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(CHANNEL_MAIN.num, mBuilder.build())

                    // 데이터 불러서 넣고
                    updateNotificationMain()
                }

            }

            //서비스 바인딩 여부 확인이 필요함//
            try {
                context.bindService( Intent(context, KillNotificationService::class.java), serviceConnection, Context.BIND_AUTO_CREATE )
            } catch (e:IllegalArgumentException){}

        }

        //
        fun closeWidtget() {

            //
            try {
                notificationManager.cancel(CHANNEL_MAIN.num)
            } catch (e:UninitializedPropertyAccessException) {}

            //
            try {
                context.unbindService(serviceConnection) // 서비스 끄고
            } catch (e:IllegalArgumentException) {
            } catch (e:UninitializedPropertyAccessException) {}

        }

        //
        fun updateNotificationMain () {

            log(tag,"updateNotificationMain ----")

            // 앱설정에서 막혀 있으면 켜지지 않음
            try {
                if(!AppSetting.notification_widget) return
            } catch (e:ExceptionInInitializerError) {
                return
            }
            // FATAL Exception !!
            //


            // 리모트 뷰가 준비되지 않았으면 가!
            //
            // ***************** 빌더가 이거 필요없다고 우기지만 꼭! 필요해요 !
            try {
                if (remoteViews == null) return
            } catch (e:UninitializedPropertyAccessException) {
                return
            }

            // 연결 상태
            if(bleConnection.STATE== BleSmartRopeState.CONNECT) {
                remoteViews.setImageViewResource(R.id.img_link, R.drawable.ic_notification_icon_link)
            } else {
                remoteViews.setImageViewResource(R.id.img_link, R.drawable.ic_notification_icon_unlink)
            }

            //
            val jump:Int = try {
                JumpToday.jsonToday.getInt("jump")
            } catch (e:JSONException) {0}
            val decFormat = DecimalFormat("#,###,###")
            val jumpString = decFormat.format(jump)
            remoteViews.setTextViewText(R.id.text_jump,jumpString)

            //
            val calorie:Float = try {
                JumpToday.jsonToday.getDouble("calorie").toFloat()
            } catch (e:JSONException) {0f}

            val calorieString = when {
                calorie==0f -> "0k"
                calorie<10 -> String.format("%.2fk",calorie)
                calorie<100 -> String.format("%.1fk",calorie)
                else -> String.format("%.0fk",calorie)
            }
            remoteViews.setTextViewText(R.id.text_calories,calorieString)

            //
            val duration:Long = try {
                JumpToday.jsonToday.getLong("duration")
            } catch (e:JSONException) {0L}
            val duration_sec = duration / 1000
            val durationString = when {
                duration_sec<60 -> {
                    // 초
                    String.format("%ds",duration_sec)
                }
                duration_sec<60*60 -> {
                    // 분
                    val duration_min = duration_sec/60.0f
                    String.format("%.1fm",duration_min)
                }
                else -> {
                    // 시
                    val duration_hour = duration_sec/3600.0f
                    String.format("%.1fh",duration_hour)
                }
            }

            remoteViews.setTextViewText(R.id.text_time,durationString)

            //
            val goal:Float = try {
                JumpToday.jsonToday.getDouble("goal").toFloat()
            } catch (e:JSONException) {0f}
            val progress = if ( (jump==0) or (goal==0f) ) {
                0f
            } else {
                (jump/goal)*100.0f
            }
            val goalString = when{
                progress==0f ->
                    "0p"
                progress<10 ->
                    String.format("%.1fp",progress)
                else ->
                 String.format("%.0fp", progress)
            }
            remoteViews.setTextViewText(R.id.text_goal,goalString)

            //
            notificationManager.notify(CHANNEL_MAIN.num, mBuilder.build())

        }

        //
        fun createChannel() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //
                val importance = NotificationManager.IMPORTANCE_LOW //IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel( CHANNEL_MAIN.id , CHANNEL_MAIN.description, importance)
                mChannel.description = CHANNEL_MAIN.description
                mChannel.setShowBadge(false) // 벳지삭제

                //
                val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)

            }

        }

        //
        fun makeNotificationPending(action:String):PendingIntent {
            val intent = Intent(context, NotificationActionReceiver::class.java )
            intent.action = action
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

    } //

}



//https://blog.geusan.com/34
// kill notification service https://stackoverflow.com/questions/12997800/cancel-notification-on-remove-application-from-multitask-panel
// https://stackoverflow.com/questions/22789588/how-to-update-notification-with-remoteviews


