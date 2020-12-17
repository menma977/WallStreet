package info.wallstreet.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.btc.SendBTCActivity

class HomeFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var level: TextView
  private lateinit var username: TextView
  private lateinit var btc: TextView
  private lateinit var btcFake: TextView
  private lateinit var ltc: TextView
  private lateinit var ltcFake: TextView
  private lateinit var eth: TextView
  private lateinit var ethFake: TextView
  private lateinit var doge: TextView
  private lateinit var dogeFake: TextView
  private lateinit var move: Intent
  private var onLogoutReady = false

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)

    level = view.findViewById(R.id.textViewLevel)
    username = view.findViewById(R.id.textViewUsername)
    btc = view.findViewById(R.id.textViewBitcoinBalance)
    btcFake = view.findViewById(R.id.textViewBitcoinBalanceFake)
    ltc = view.findViewById(R.id.textViewLiteCoinBalance)
    ltcFake = view.findViewById(R.id.textViewLiteCoinBalanceFake)
    eth = view.findViewById(R.id.textViewEthereumBalance)
    ethFake = view.findViewById(R.id.textViewEthereumBalanceFake)
    doge = view.findViewById(R.id.textViewDogeCoinBalance)
    dogeFake = view.findViewById(R.id.textViewDogeCoinBalanceFake)

    level.text = user.getString("level")
    username.text = user.getString("username")

    defaultBalance()

    btc.setOnClickListener {
      move = Intent(parentActivity, SendBTCActivity::class.java)
      move.putExtra("title", "BTC")
      move.putExtra("fake", false)
      startActivity(move)
    }

    btcFake.setOnClickListener {
      move = Intent(parentActivity, SendBTCActivity::class.java)
      move.putExtra("title", "BTC Wall")
      move.putExtra("fake", true)
      startActivity(move)
    }

    return view
  }

  private fun defaultBalance() {
    /**
     * ture balance
     */
    if (user.getString("balance_btc").isNotEmpty()) {
      btc.text = CoinFormat.decimalToCoin(user.getString("balance_btc").toBigDecimal()).toPlainString()
    } else {
      btc.text = "0"
    }

    if (user.getString("balance_doge").isNotEmpty()) {
      doge.text = CoinFormat.decimalToCoin(user.getString("balance_doge").toBigDecimal()).toPlainString()
    } else {
      doge.text = "0"
    }

    if (user.getString("balance_ltc").isNotEmpty()) {
      ltc.text = CoinFormat.decimalToCoin(user.getString("balance_ltc").toBigDecimal()).toPlainString()
    } else {
      ltc.text = "0"
    }

    if (user.getString("balance_eth").isNotEmpty()) {
      eth.text = CoinFormat.decimalToCoin(user.getString("balance_eth").toBigDecimal()).toPlainString()
    } else {
      eth.text = "0"
    }
    /**
     * Fake Balance
     */
    if (user.getString("fake_balance_btc").isNotEmpty()) {
      btcFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_btc").toBigDecimal()).toPlainString()
    } else {
      btcFake.text = "0"
    }

    if (user.getString("fake_balance_doge").isNotEmpty()) {
      dogeFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_doge").toBigDecimal()).toPlainString()
    } else {
      dogeFake.text = "0"
    }

    if (user.getString("fake_balance_ltc").isNotEmpty()) {
      ltcFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_ltc").toBigDecimal()).toPlainString()
    } else {
      ltcFake.text = "0"
    }

    if (user.getString("fake_balance_eth").isNotEmpty()) {
      ethFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_eth").toBigDecimal()).toPlainString()
    } else {
      ethFake.text = "0"
    }
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverBtc, IntentFilter("web.btc"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverDoge, IntentFilter("web.doge"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverEth, IntentFilter("web.eth"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverLtc, IntentFilter("web.ltc"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  private var broadcastReceiverBtc: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        btcFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_btc").toBigDecimal()).toPlainString()
      }
    }
  }
  private var broadcastReceiverDoge: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        dogeFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_doge").toBigDecimal()).toPlainString()
      }
    }
  }
  private var broadcastReceiverLtc: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        ltcFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_ltc").toBigDecimal()).toPlainString()
      }
    }
  }
  private var broadcastReceiverEth: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        ethFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_eth").toBigDecimal()).toPlainString()
      }
    }
  }
}