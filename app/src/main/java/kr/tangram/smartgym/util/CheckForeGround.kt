/*
 * Copyright (c) 2020 TANGRAM FACTORY, INC. All rights reserved.
 * @auth kang@tangram.kr
 * @date 10/5/20 4:49 PM.
 */

package kr.tangram.smartgym.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.AsyncTask

internal class CheckForeGround : AsyncTask<Context?, Void?, Boolean?>() {
	private fun isAppOnForeground(context : Context?) : Boolean {
		val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val appProcesses = activityManager.runningAppProcesses ?: return false
		val packageName : String = context.packageName
		for (appProcess in appProcesses) {
			if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
				return true
			}
		}
		return false
	}
	override fun doInBackground(vararg params : Context?) : Boolean? {
		val context:Context? = params[0]?.applicationContext
		return isAppOnForeground(context)
	}
}