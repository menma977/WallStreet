package info.wallstreet.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.controller.DogeController
import info.wallstreet.controller.GetController
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
    return START_STICKY
  }

  private fun onHandleIntent() {
    user = User(this)
    var time = System.currentTimeMillis()
    val trigger = Object()

    Timer().schedule(5000) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 10000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            synchronized(trigger) {
              try {
                val body = FormBody.Builder()
                body.addEncoded("a", "GetBalances")
                body.addEncoded("key", "1b4755ced78e4d91bce9128b9a053cad")
                body.addEncoded("s", user.getString("cookie"))
                json = DogeController(body).call()
                if (json.getInt("code") == 200) {
                  val balances = json.getJSONObject("data").getJSONArray("Balances")
                  if (balances.length() > 0) {
                    for (i in 0 until balances.length()) {
                      val balance = balances.getJSONObject(i)
                      val currency = balance.getString("Currency")
                      user.setString("balance_$currency", balance.getString("Balance"))
                    }
                  }

                  val response = GetController("camel.balances", user.getString("token")).call()
                  if (response.getInt("code") < 400) {
                    user.setString("balance_tron", response.getJSONObject("data").getString("tron"))
                    user.setString("balance_camel", response.getJSONObject("data").getString("camel"))
                    user.setString("balance_gold", response.getJSONObject("data").getString("gold"))
                  }

                  privateIntent.action = "doge.balances"
                  LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
                } else {
                  trigger.wait(60000)
                }
              } catch (e: Exception) {
                Log.w("error balance service", e.message.toString())
              }
            }
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