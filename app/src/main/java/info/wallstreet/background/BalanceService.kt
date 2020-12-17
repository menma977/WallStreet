package info.wallstreet.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.controller.DogeController
import info.wallstreet.model.User
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class BalanceService : Service() {
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
    val trigger = Object()

    Timer().schedule(5000) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 15000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            synchronized(trigger) {
              fetchBalance("btc", privateIntent, trigger)
              fetchBalance("ltc", privateIntent, trigger)
              fetchBalance("eth", privateIntent, trigger)
              fetchBalance("doge", privateIntent, trigger)
            }
          } else {
            break
          }
        }
      }
    }
  }

  private fun fetchBalance(type: String, privateIntent: Intent, trigger: Object) {
    try {
      val body = FormBody.Builder()
      body.addEncoded("a", "GetBalance")
      body.addEncoded("key", "1b4755ced78e4d91bce9128b9a053cad")
      body.addEncoded("s", user.getString("cookie"))
      body.addEncoded("Currency", "doge")
      json = DogeController(body).call()
      if (json.getInt("code") == 200) {
        if (json.getJSONObject("data").getString("Balance").isEmpty()) {
          user.setString("balance_$type", "0")
        } else {
          user.setString("balance_$type", json.getJSONObject("data").getString("Balance"))
        }
        privateIntent.action = "api.doge"
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
      } else {
        trigger.wait(60000)
      }
    } catch (e: Exception) {
      Log.w("error", e.message.toString())
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