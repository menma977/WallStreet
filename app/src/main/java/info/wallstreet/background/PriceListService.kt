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

class PriceListService : Service() {
  private lateinit var result: JSONObject
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
        if (delta >= 60000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            result = GetController("upgrade.price", user.getString("token")).call()
            println(result)
            when {
              result.getInt("code") == 200 -> {
                user.setString("doge_price", result.getJSONObject("data").getString("doge"))
                user.setString("btc_price", result.getJSONObject("data").getString("btc"))
                user.setString("eth_price", result.getJSONObject("data").getString("eth"))
                user.setString("ltc_price", result.getJSONObject("data").getString("ltc"))
                user.setString("camel_price", result.getJSONObject("data").getString("camel"))
                user.setString("tron_price", result.getJSONObject("data").getString("tron"))

                Thread.sleep(1000)
              }
              result.getBoolean("logout") -> {
                defaultPrice()
                user.setBoolean("logout", true)
                Thread.sleep(2000)
                stopSelf()
              }
              else -> {
                defaultPrice()
                user.setBoolean("logout", false)
                Thread.sleep(20000)
              }
            }

            privateIntent.action = "web.price.list"
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

  private fun defaultPrice() {
    if (user.getString("btc_price").isEmpty()) {
      user.setString("btc_price", "0")
    }
    if (user.getString("eth_price").isEmpty()) {
      user.setString("eth_price", "0")
    }
    if (user.getString("doge_price").isEmpty()) {
      user.setString("doge_price", "0")
    }
    if (user.getString("ltc_price").isEmpty()) {
      user.setString("ltc_price", "0")
    }
    if (user.getString("camel_price").isEmpty()) {
      user.setString("camel_price", "0")
    }
    if (user.getString("tron_price").isEmpty()) {
      user.setString("tron_price", "0")
    }
  }
}