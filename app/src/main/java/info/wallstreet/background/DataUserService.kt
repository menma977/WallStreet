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

class DataUserService : Service() {
  private lateinit var result: JSONObject
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
        if (delta >= 20000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            result = GetController("user.show", user.getString("token")).call()
            println(result)
            when {
              result.getInt("code") == 200 -> {
                user.setString("cookie", result.getJSONObject("data").getString("cookie"))
                user.setString("email", result.getJSONObject("data").getString("email"))
                user.setString("username", result.getJSONObject("data").getString("username"))
                user.setString("phone", result.getJSONObject("data").getString("phone"))
                user.setString("wallet_btc", result.getJSONObject("data").getString("wallet_btc"))
                user.setString("wallet_doge", result.getJSONObject("data").getString("wallet_doge"))
                user.setString("wallet_ltc", result.getJSONObject("data").getString("wallet_ltc"))
                user.setString("wallet_eth", result.getJSONObject("data").getString("wallet_eth"))
                user.setString("level", result.getJSONObject("data").getString("level"))
                Thread.sleep(1000)
              }
              result.getBoolean("logout") -> {
                user.setBoolean("logout", true)
                Thread.sleep(2000)
                stopSelf()
              }
              else -> {
                user.setBoolean("logout", false)
                Thread.sleep(20000)
              }
            }

            privateIntent.action = "web.user"
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