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

    init {
        val option = FirebaseOptions.Builder()
            .setProjectId(context.getString(R.string.firebase_project_id))
            .setApplicationId(context.getString(R.string.firebase_application_id))
            .setApiKey(context.getString(R.string.firebase_api_key))
            .build()
        //중복요청 에러 방지
        if (FirebaseApp.getApps(context).isNullOrEmpty()) {
            Firebase.initialize(context, option, FirebaseApp.DEFAULT_APP_NAME)
        }
    }

    fun sendEmail(email: String, emailReceiveType: EmailReceiveType, name : String="", birthDay : String="", gender: String="", parentUid : String?="") {
        Firebase.auth.sendSignInLinkToEmail(email, makeSetting(email, emailReceiveType, name, birthDay,gender, parentUid))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "Email sent.")
                }
            }
            .addOnFailureListener {
                Log.d("Login", "fail ${it.message}")
            }
    }

    private fun makeSetting(
        email: String,
        emailReceiveType: EmailReceiveType,
        name: String,
        birthDay: String,
        gender: String,
        parentUid: String?
    ): ActionCodeSettings {
        var url = "https://smartgym.page.link/Login"

        when (emailReceiveType) {
            EmailReceiveType.Login -> url = Uri.parse(url).buildUpon()
                .appendQueryParameter("receiveEmail", "Login")
                .appendQueryParameter("email", email)
                .build()
                .toString()

            EmailReceiveType.Join -> url = Uri.parse(url).buildUpon()
                .appendQueryParameter("receiveEmail", "Join")
                .appendQueryParameter("email", email)
                .build()
                .toString()

            EmailReceiveType.Junior -> url = Uri.parse(url).buildUpon()
                .appendQueryParameter("receiveEmail", "Junior")
                .appendQueryParameter("name", name)
                .appendQueryParameter("birthDay", birthDay)
                .appendQueryParameter("email", email)
                .appendQueryParameter("parentUid", parentUid!!)
                .appendQueryParameter("gender", gender)
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


sealed class EmailReceiveType {
    object Login : EmailReceiveType()
    object Join : EmailReceiveType()
    object Junior : EmailReceiveType()
    data class Fail(val data: String) : EmailReceiveType()
    data class FailToast(val data: String) : EmailReceiveType()
}