package info.wallstreet.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import org.json.JSONObject
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.schedule

class CamelService : Service() {
  private lateinit var json: JSONObject
  private lateinit var user: User
  private var startBackgroundService: Boolean = false

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    onHandleIntent()

    return START_STICKY
  }

  private fun onHandleIntent() {
    user = User(this)
    var time = System.currentTimeMillis()

    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 10000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            json = GetController("camel", user.getString("token")).call()
            when {
              json.getInt("code") == 200 -> {
                user.setString("fake_balance_camel", json.getJSONObject("data").getString("balance"))
                if (json.getJSONObject("data").getInt("on_queue") > 0) {
                  user.setBoolean("on_queue", true)
                } else {
                  user.setBoolean("on_queue", false)
                }
                sleep(1000)
              }
              json.getBoolean("logout") -> {
                user.setBoolean("logout", true)
                user.setBoolean("on_queue", true)
                sleep(2000)
                stopSelf()
              }
              else -> {
                user.setBoolean("logout", false)
                user.setBoolean("on_queue", true)
                sleep(20000)
              }
            }

            privateIntent.action = "web.camel"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
          } else {
            stopSelf()
          }
        }
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    startBackgroundService = true
  }

  override fun onDestroy() {
    super.onDestroy()
    startBackgroundService = false
  }

  override fun onBind(intent: Intent?): IBinder? {
    TODO("Not yet implemented")
  }
}