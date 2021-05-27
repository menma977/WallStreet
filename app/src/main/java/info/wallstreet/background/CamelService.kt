package info.wallstreet.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.schedule
import org.json.JSONObject

class CamelService : Service() {
  private lateinit var json: JSONObject
  private lateinit var user: User
  private var startBackgroundService: Boolean = false

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_STICKY
  }

  private fun onHandleIntent() {
    user = User(this)
    var time = System.currentTimeMillis()

    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 5000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            json = GetController("camel", user.getString("token")).call()
            when {
              json.getInt("code") == 200 -> {
                user.setString("fake_balance_camel", json.getJSONObject("data").getString("balance"))
                user.setString("fake_balance_gold", json.getJSONObject("data").getString("balance_gold"))
                if (json.getJSONObject("data").getInt("on_queue") > 0) {
                  user.setBoolean("on_queue", true)
                } else {
                  user.setBoolean("on_queue", false)
                }
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
    onHandleIntent()
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
