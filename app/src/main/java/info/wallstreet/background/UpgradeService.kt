package info.wallstreet.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class UpgradeService : Service() {
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
        if (delta >= 8000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            json = GetController("upgrade", user.getString("token")).call()
            when {
              json.getInt("code") == 200 -> {
                user.setInteger("progress", json.getJSONObject("data").getInt("progress"))
                user.setString("progressValue", json.getJSONObject("data").getString("progress_value"))
                user.setString("targetValue", json.getJSONObject("data").getString("target"))
                Thread.sleep(1000)
              }
              json.getBoolean("logout") -> {
                user.setBoolean("logout", true)
                Thread.sleep(2000)
                stopSelf()
              }
              else -> {
                user.setBoolean("logout", false)
                Thread.sleep(20000)
              }
            }

            privateIntent.action = "web.upgrade"
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