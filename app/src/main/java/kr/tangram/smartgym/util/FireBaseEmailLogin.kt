package kr.tangram.smartgym.util

import android.content.Context
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

    private val actionCodeSettings: ActionCodeSettings = actionCodeSettings {
        url = "https://smartgym.page.link/Login?"
        // This must be true
        handleCodeInApp = true
        setAndroidPackageName(
            "kr.tangramfactory.smartgym",
            false, /* installIfNotAvailable */
            "21" /* minimumVersion */)
    }

    fun sendEmail(email : String) {
        Firebase.auth.sendSignInLinkToEmail(email,actionCodeSettings)

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "Email sent.")
                }
            }
            .addOnFailureListener {
                Log.d("Login", "fail ${it.message}")
            }
    }
}