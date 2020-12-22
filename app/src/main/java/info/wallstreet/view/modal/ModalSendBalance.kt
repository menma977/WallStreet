package info.wallstreet.view.modal

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import info.wallstreet.R
import info.wallstreet.view.coin.SendCoinActivity
import java.util.*

class ModalSendBalance constructor(context: Context) : AlertDialog(context) {
  private var btc: Button
  private var btcWall: Button
  private var eth: Button
  private var ethWall: Button
  private var doge: Button
  private var dogeWall: Button
  private var ltc: Button
  private var ltcWall: Button
  private var close: ImageView
  private lateinit var move: Intent

  init {
    val layout: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = layout.inflate(R.layout.modal_send_layout, LinearLayout(context), false)
    if (context is Activity) setOwnerActivity(context)
    setView(view)

    btc = view.findViewById(R.id.buttonBTC)
    btcWall = view.findViewById(R.id.buttonBTCWall)
    eth = view.findViewById(R.id.buttonETH)
    ethWall = view.findViewById(R.id.buttonETHWall)
    doge = view.findViewById(R.id.buttonDoge)
    dogeWall = view.findViewById(R.id.buttonDogeWall)
    ltc = view.findViewById(R.id.buttonLTC)
    ltcWall = view.findViewById(R.id.buttonLTCWall)
    close = view.findViewById(R.id.buttonClose)

    close.setOnClickListener {
      dismiss()
    }

    btc.setOnClickListener {
      sendCoin("btc", false)
    }

    btcWall.setOnClickListener {
      sendCoin("btc", true)
    }

    ltc.setOnClickListener {
      sendCoin("ltc", false)
    }

    ltcWall.setOnClickListener {
      sendCoin("ltc", true)
    }

    eth.setOnClickListener {
      sendCoin("eth", false)
    }

    ethWall.setOnClickListener {
      sendCoin("eth", true)
    }

    doge.setOnClickListener {
      sendCoin("doge", false)
    }

    dogeWall.setOnClickListener {
      sendCoin("doge", true)
    }

  }

  private fun sendCoin(currency: String, fake: Boolean) {
    move = Intent(context, SendCoinActivity::class.java)
    move.putExtra("title", currency.toUpperCase(Locale.getDefault()) + if (fake) " Wall" else "")
    move.putExtra("currency", currency.toLowerCase(Locale.getDefault()))
    move.putExtra("fake", fake)
    context.startActivity(move)
  }
}