/*
 * Copyright (c) 2020 TANGRAM FACTORY, INC. All rights reserved.
 * @auth kang@tangram.kr
 * @date 7/6/20 11:56 AM.
 */

package com.tangramfactory.smartrope.common.func

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth

import kr.tangram.smartgym.util.log
import org.json.JSONObject
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class SAuth {

    enum class AUTH_TYPE { NONE, GLOBAL, CHINA }

    // --
    companion object {

        // --
        var tag: String = javaClass.name

        var type: AUTH_TYPE = AUTH_TYPE.NONE
//            set(value) {
//                if (value == AUTH_TYPE.CHINA) {
//                    FireStoreServer = getProxyServer() // 서버 중국
//                    field = AUTH_TYPE.CHINA
////					Preferences.set("china",true)
//                } else {
//                    FireStoreServer = getMainServer() // 서버 미국
//                    field = AUTH_TYPE.GLOBAL
////					Preferences.set("china",false)
//                }
//            }

        var userInfo: JSONObject? = null
            //			get() {
//				val auth = Preferences.get("auth")
//				log(tag,"get userinfo auth : " + cryptor().decrypt(auth.toString())!! )
//				return if(auth!=null) {
//					JSONObject(cryptor().decrypt(auth)!!)
//				} else {
//					null
//				}
//			}
//            set(value) {
//                log(tag, "save userinfo " + value)
//                if (value != null) {
//                    val auth = cryptor().encrypt(value.toString())
////                    Preferences.set("auth", auth.toString())
//                }
//                field = value
//            }
        val uid: String?
            get() {
                return if (type == AUTH_TYPE.CHINA) {
                    try {
                        userInfo?.getString("uid")
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    FirebaseAuth.getInstance().currentUser?.uid
                }
            }
        val email: String?
            get() {
                return if (type == AUTH_TYPE.CHINA) {

                    try {
                        userInfo?.getString("email")
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    FirebaseAuth.getInstance().currentUser?.email
                }
            }
        val token: String?
            get() {
                return if (type == AUTH_TYPE.CHINA) {
                    try {
                        userInfo?.getString("token")
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            }
        val displayName: String?
            get() {
                return if (type == AUTH_TYPE.CHINA) {
//                    val person = Preferences.getJson("personinfo")
//                    val person = Preferences.getJson("personinfo")
//                    try {
//                        person?.getString("name")
//                    } catch (e: Exception) {
//                        null
//                    }
                    null
                } else {
                    FirebaseAuth.getInstance().currentUser?.displayName
                }
            }
        val logined: Boolean
            get() {
                // -- 로그인 체크
                return if (type == AUTH_TYPE.CHINA) {
                    log(tag, "1 LOGINED .. " + (userInfo != null))
                    userInfo != null
                } else {
                    log(tag, "2 LOGINED .. " + (FirebaseAuth.getInstance().currentUser != null))
                    FirebaseAuth.getInstance().currentUser != null
                }
            }

        // -- --  -- --  -- --  -- --  -- --  -- --  -- --
        init {
            log(tag, "---------- SAuth init..")
            //
            if (FirebaseAuth.getInstance().currentUser != null) {
                type = AUTH_TYPE.GLOBAL
            } else {
                // 저장된 정보가져옴
//                val auth = Preferences.get("auth")
//                if (auth.isNotEmpty()) {
////                    log(tag, "--- " + cryptor().decrypt(auth)!!)
//                    userInfo = JSONObject(cryptor().decrypt(auth)!!)
//                    type = AUTH_TYPE.CHINA
//                }
            }

        }

        // -- 사용자 정보 삭제
        fun delete() {
            try {
//                Preferences.clear("auth")
//				Preferences.clear("china")
            } catch (e: Exception) {
                log(tag, e.toString())
            }
        }

        //		//
//		fun login(json:JSONObject) {
//			log(tag,"save userinfo " + json )
//			val auth = cryptor().encrypt(json.toString())
//			Preferences.set("auth",auth.toString())
//		}
        fun logout() {
            delete()
            userInfo = null
            type = AUTH_TYPE.NONE
        }

        // -- AUTH (email/fid)가 있는 json
        fun makeJSON(): JSONObject {
            val json = JSONObject()
            if (type == AUTH_TYPE.GLOBAL && FirebaseAuth.getInstance().currentUser != null) {
                json.put("email", FirebaseAuth.getInstance().currentUser?.email)
                json.put("fid", FirebaseAuth.getInstance().currentUser?.uid)
            }
//			log(tag,"... " + type)
//			log(tag,"... " + FirebaseAuth.getInstance().currentUser )
//			log(tag,"... " + json )
            return json
        }

    }

    // --
    class cryptor {
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
        private val ALIAS = "smartrope"

        @RequiresApi(Build.VERSION_CODES.M)
        private fun createKeys(): KeyStore.Entry {

            //
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            val containsAlias = keyStore.containsAlias(ALIAS)

            //
            if (!containsAlias) {
                val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
                val start = Calendar.getInstance(Locale.ENGLISH)
                val end = Calendar.getInstance(Locale.ENGLISH)
                end.add(Calendar.YEAR, 3) // -- 유예기간

                //
                val spec = KeyGenParameterSpec.Builder(
						ALIAS,
						KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setCertificateSubject(X500Principal("CN=$ALIAS"))
                        .setCertificateSerialNumber(BigInteger.ONE)
                        .setKeyValidityStart(start.time)
                        .setKeyValidityEnd(end.time)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build()

                //
                kpg.initialize(spec)
                kpg.generateKeyPair()
            }
            return keyStore.getEntry(ALIAS, null)
        }

        private fun encryptUsingKey(publicKey: PublicKey, bytes: ByteArray): ByteArray {
            val inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return inCipher.doFinal(bytes)
        }

        private fun decryptUsingKey(privateKey: PrivateKey, bytes: ByteArray): ByteArray {
            val inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            inCipher.init(Cipher.DECRYPT_MODE, privateKey)
            return inCipher.doFinal(bytes)
        }

        // --
        @RequiresApi(Build.VERSION_CODES.M)
        fun encrypt(plainText: String): String? {
            val entry = createKeys()
            if (entry is KeyStore.PrivateKeyEntry) {
                val certificate = entry.certificate
                val publicKey = certificate.publicKey
                val bytes = plainText.toByteArray(charset("UTF-8"))
                val encryptedBytes = encryptUsingKey(publicKey, bytes)
                val base64encryptedBytes: ByteArray = android.util.Base64.encode(encryptedBytes, android.util.Base64.DEFAULT)
                return String(base64encryptedBytes)
            }
            return null
        }

        // --
        fun decrypt(cipherText: String): String? {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            val entry = keyStore.getEntry(ALIAS, null)
            if (entry is KeyStore.PrivateKeyEntry) {
                val privateKey = entry.privateKey
                val bytes = cipherText.toByteArray(charset("UTF-8"))
                val base64encryptedBytes: ByteArray = android.util.Base64.decode(bytes, android.util.Base64.DEFAULT)
                val decryptedBytes = decryptUsingKey(privateKey, base64encryptedBytes)
                return String(decryptedBytes)
            }
            return null
        }

    }
}