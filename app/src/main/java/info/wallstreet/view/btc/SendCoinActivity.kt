package info.wallstreet.view.btc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.zxing.Result
import info.wallstreet.MainActivity
import info.wallstreet.R
import info.wallstreet.background.BtcService
import info.wallstreet.background.LtcService
import info.wallstreet.background.EthService
import info.wallstreet.background.DogeService
import info.wallstreet.config.CoinFormat
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.FormBody
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.concurrent.schedule

class SendCoinActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var json: JSONObject
  private lateinit var frameScanner: FrameLayout
  private lateinit var scannerEngine: ZXingScannerView
  private lateinit var wallet: String
  private lateinit var title: TextView
  private lateinit var currentBalance: TextView
  private lateinit var balanceText: EditText
  private lateinit var sendDoge: Button
  private lateinit var walletText: EditText
  private lateinit var secondaryPasswordText: EditText
  private lateinit var receiver: Intent
  private lateinit var balanceValue: BigDecimal
  private var currency = ""
  private var isHasCode = false
  private var isStart = true
  private var isFake = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_fake_balance)

    user = User(this)
    loading = Loading(this)

    title = findViewById(R.id.textViewTitle)
    currentBalance = findViewById(R.id.textViewBalance)
    walletText = findViewById(R.id.editTextWallet)
    frameScanner = findViewById(R.id.frameLayoutScanner)
    balanceText = findViewById(R.id.editTextBalance)
    secondaryPasswordText = findViewById(R.id.editTextSecondaryPassword)
    sendDoge = findViewById(R.id.buttonSend)

    title.text = intent.getStringExtra("title")
    isFake = intent.getBooleanExtra("fake", true)
    currency = intent.getStringExtra("title") ?: "btc"

    currentBalance.text = if (isFake) {
      balanceValue = user.getString("fake_balance_$currency").toBigDecimal()
      CoinFormat.decimalToCoin(user.getString("fake_balance_$currency").toBigDecimal())
        .toPlainString() + " " + currency.toUpperCase(
        Locale.getDefault()
      )
    } else {
      balanceValue = user.getString("balance_$currency").toBigDecimal()
      CoinFormat.decimalToCoin(user.getString("balance_$currency").toBigDecimal())
        .toPlainString() + " " + currency.toUpperCase(
        Locale.getDefault()
      )
    }

    runService()
    initScannerView()

    frameScanner.setOnClickListener {
      if (isStart) {
        scannerEngine.startCamera()
        isStart = false
      }
    }

    sendDoge.setOnClickListener {
      when {
        balanceText.text.isEmpty() -> {
          Toast.makeText(this, "Amount required", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        CoinFormat.coinToDecimal(balanceText.text.toString().toBigDecimal()) > balanceValue -> {
          Toast.makeText(this, "Amount exceeds the maximum balance", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        secondaryPasswordText.text.isEmpty() -> {
          Toast.makeText(this, "Secondary Password required", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        secondaryPasswordText.text.length < 6 -> {
          Toast.makeText(this, "Secondary password must be 6 digit numbers", Toast.LENGTH_SHORT)
            .show()
          secondaryPasswordText.requestFocus()
        }
        else -> {
          loading.openDialog()
          onSend()
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    runService()
  }

  private fun onSend() {
    val body = FormBody.Builder()
    body.addEncoded("wallet", walletText.text.toString())
    body.addEncoded(
      "value",
      CoinFormat.coinToDecimal(balanceText.text.toString().toBigDecimal()).toEngineeringString()
    )
    body.addEncoded("secondary_password", secondaryPasswordText.text.toString())
    body.addEncoded("fake", isFake.toString())
    Timer().schedule(100) {
      json = PostController("$currency.store", user.getString("token"), body).call()
      if (json.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(
            applicationContext,
            json.getJSONObject("data").getString("message"),
            Toast.LENGTH_LONG
          ).show()
          loading.closeDialog()
          finish()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, json.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun runService() {
    Timer().schedule(1000) {
      if (isFake) {
        when (currency) {
          "btc" -> {
            receiver = Intent(applicationContext, BtcService::class.java)
          }
          "ltc" -> {
            receiver = Intent(applicationContext, LtcService::class.java)
          }
          "eth" -> {
            receiver = Intent(applicationContext, EthService::class.java)
          }
          "doge" -> {
            receiver = Intent(applicationContext, DogeService::class.java)
          }
        }
        startService(receiver)
        LocalBroadcastManager.getInstance(applicationContext)
          .registerReceiver(broadcastReceiver, IntentFilter("web.$currency"))
      }
    }
  }

  override fun onStop() {
    super.onStop()
    if (isFake) {
      LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
      stopService(receiver)
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    if (isFake) {
      stopService(receiver)
      finish()
    }
  }

  private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (user.getBoolean("logout")) {
        onLogout()
      } else {
        val balance = if (isFake) {
          CoinFormat.decimalToCoin(user.getString("fake_balance_$currency").toBigDecimal())
            .toPlainString() + " " + currency.toUpperCase(
            Locale.getDefault()
          )
        } else {
          CoinFormat.decimalToCoin(user.getString("balance_$currency").toBigDecimal())
            .toPlainString() + " " + currency.toUpperCase(
            Locale.getDefault()
          )
        }

        currentBalance.text = balance
      }
    }
  }

  private fun onLogout() {
    Timer().schedule(100) {
      GetController("logout", user.getString("token")).call()
      runOnUiThread {
        user.clear()
        val move = Intent(applicationContext, MainActivity::class.java)
        loading.closeDialog()
        startActivity(move)
        finishAffinity()
      }
    }
  }

  override fun handleResult(rawResult: Result?) {
    if (rawResult?.text?.isNotEmpty()!!) {
      isHasCode = true
      wallet = rawResult.text.toString()
      walletText.setText(wallet)
    } else {
      isHasCode = false
    }
  }

  private fun initScannerView() {
    scannerEngine = ZXingScannerView(this)
    scannerEngine.setAutoFocus(true)
    scannerEngine.setResultHandler(this)
    frameScanner.addView(scannerEngine)
  }

  override fun onPause() {
    scannerEngine.stopCamera()
    super.onPause()
  }
}