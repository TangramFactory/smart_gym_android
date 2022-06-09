package kr.tangram.smartgym.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kr.tangram.smartgym.R

class FireBaseEmailLogin constructor(private val context: Context) {
    private val TAG = "deepLink"
    init {
        val option = FirebaseOptions.Builder()
            .setProjectId(context.getString(R.string.firebase_project_id))
            .setApplicationId(context.getString(R.string.firebase_application_id))
            .setApiKey(context.getString(R.string.firebase_api_key))
            .build()
        //중복요청 에러 방지
        if (FirebaseApp.getApps(context).isNullOrEmpty()) {
            Firebase.initialize(context,option, FirebaseApp.DEFAULT_APP_NAME)
        }
    }

    fun sendEmail(email : String, receiveEmail: ReceiveEmail) {
        Firebase.auth.sendSignInLinkToEmail(email,makeSetting(receiveEmail))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "Email sent.")
                }
            }
            .addOnFailureListener {
                Log.d("Login", "fail ${it.message}")
            }
    }

    private fun makeSetting(receiveEmail: ReceiveEmail) : ActionCodeSettings{
        var url = "https://smartgym.page.link/Login"

        when (receiveEmail) {
            ReceiveEmail.LOGIN -> url = Uri.parse(url).buildUpon()
                    .appendQueryParameter("receiveEmail", ReceiveEmail.LOGIN.toString())
                    .build()
                    .toString()

            ReceiveEmail.JOIN -> url = Uri.parse(url).buildUpon()
                .appendQueryParameter("receiveEmail", ReceiveEmail.JOIN.toString())
                .build()
                .toString()

            ReceiveEmail.JUNIOR -> url = Uri.parse(url).buildUpon()
                .appendQueryParameter("receiveEmail", ReceiveEmail.JUNIOR.toString())
                .build()
                .toString()
        }

        return actionCodeSettings {
            this.url = url
            // This must be true
            handleCodeInApp = true
            setAndroidPackageName(
                "kr.tangramfactory.smartgym",
                false, /* installIfNotAvailable */
                "21" /* minimumVersion */)
        }
    }
}


enum class ReceiveEmail{
    LOGIN, JOIN, JUNIOR
}