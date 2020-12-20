package info.wallstreet.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.UpgradeHistoryActivity
import info.wallstreet.view.coin.SendCoinActivity
import info.wallstreet.view.history.FakeBalanceActivity
import info.wallstreet.view.modal.UpgradePop
import info.wallstreet.view.modal.WalletQR
import java.util.*

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
  private lateinit var toBTCWallet: ImageView
  private lateinit var toLTCWallet: ImageView
  private lateinit var toETHWallet: ImageView
  private lateinit var toDOGEWallet: ImageView
  private lateinit var toSendBTC: ImageView
  private lateinit var toSendLTC: ImageView
  private lateinit var toSendETH: ImageView
  private lateinit var toSendDOGE: ImageView
  private lateinit var toSendBTCFake: ImageView
  private lateinit var toSendLTCFake: ImageView
  private lateinit var toSendETHFake: ImageView
  private lateinit var toSendDOGEFake: ImageView
  private lateinit var progressBar: ProgressBar
  private lateinit var progressValue: TextView
  private lateinit var targetValue: TextView
  private lateinit var inviteBtn: Button
  private lateinit var upgradeBtn: LinearLayout
  private lateinit var historyUpgradeButton: ImageView
  private lateinit var historyWall: LinearLayout
  private lateinit var move: Intent
  private var onLogoutReady = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
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

    toBTCWallet = view.findViewById(R.id.toBtcWallet)
    toLTCWallet = view.findViewById(R.id.toLtcWallet)
    toETHWallet = view.findViewById(R.id.toEthWallet)
    toDOGEWallet = view.findViewById(R.id.toDogeWallet)

    toSendBTC = view.findViewById(R.id.wallet_bitcoin_view)
    toSendLTC = view.findViewById(R.id.wallet_litecoin_view)
    toSendETH = view.findViewById(R.id.wallet_ethereum_view)
    toSendDOGE = view.findViewById(R.id.wallet_dogecoin_view)

    toSendBTCFake = view.findViewById(R.id.wallet_fake_bitcoin_view)
    toSendLTCFake = view.findViewById(R.id.wallet_fake_litecoin_view)
    toSendETHFake = view.findViewById(R.id.wallet_fake_ethereum_view)
    toSendDOGEFake = view.findViewById(R.id.wallet_fake_dogecoin_view)

    inviteBtn = view.findViewById(R.id.invite)
    upgradeBtn = view.findViewById(R.id.buttonUpgrade)
    historyUpgradeButton = view.findViewById(R.id.history_upgrades)
    historyWall = view.findViewById(R.id.buttonHistoryWall)

    progressBar = view.findViewById(R.id.progressBar)
    progressValue = view.findViewById(R.id.textViewProgressBar)
    targetValue = view.findViewById(R.id.textViewTarget)

    level.text = "Level : ${user.getString("level")}"
    username.text = user.getString("username")

    defaultBalance()

    upgradeBtn.setOnClickListener {
      UpgradePop(parentActivity, user).show()
    }

    historyUpgradeButton.setOnClickListener {
      move = Intent(parentActivity, UpgradeHistoryActivity::class.java)
      startActivity(move)
    }

    historyWall.setOnClickListener {
      move = Intent(parentActivity, FakeBalanceActivity::class.java)
      startActivity(move)
    }

    toSendBTC.setOnClickListener {
      sendCoin("btc", false)
    }

    toSendBTCFake.setOnClickListener {
      sendCoin("btc", true)
    }

    toSendLTC.setOnClickListener {
      sendCoin("ltc", false)
    }

    toSendLTCFake.setOnClickListener {
      sendCoin("ltc", true)
    }

    toSendETH.setOnClickListener {
      sendCoin("eth", false)
    }

    toSendETHFake.setOnClickListener {
      sendCoin("eth", true)
    }

    toSendDOGE.setOnClickListener {
      sendCoin("doge", false)
    }

    toSendDOGEFake.setOnClickListener {
      sendCoin("doge", true)
    }

    toBTCWallet.setOnClickListener {
      WalletQR.show(parentActivity, "btc", user)
    }

    toLTCWallet.setOnClickListener {
      WalletQR.show(parentActivity, "ltc", user)
    }

    toETHWallet.setOnClickListener {
      WalletQR.show(parentActivity, "eth", user)
    }

    toDOGEWallet.setOnClickListener {
      WalletQR.show(parentActivity, "doge", user)
    }

    return view
  }

  private fun sendCoin(currency: String, fake: Boolean) {
    move = Intent(parentActivity, SendCoinActivity::class.java)
    move.putExtra("title", currency.toUpperCase(Locale.getDefault()) + if (fake) " Wall" else "")
    move.putExtra("currency", currency.toLowerCase(Locale.getDefault()))
    move.putExtra("fake", fake)
    startActivity(move)
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
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverBalances, IntentFilter("doge.balances"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverUpgrade, IntentFilter("web.upgrade"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverBtc, IntentFilter("web.btc"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverDoge, IntentFilter("web.doge"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverEth, IntentFilter("web.eth"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverLtc, IntentFilter("web.ltc"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
  }

  private var broadcastReceiverUpgrade: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        progressBar.progress = user.getInteger("progress")
        val getProgressValue = "$ ${CoinFormat.toDollar(user.getString("progressValue").toBigDecimal()).toPlainString()}"
        val getTargetValue = "$ ${CoinFormat.toDollar(user.getString("targetValue").toBigDecimal()).toPlainString()}"
        progressValue.text = getProgressValue
        targetValue.text = getTargetValue
      }
    }
  }
  private var broadcastReceiverBalances: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        btc.text = CoinFormat.decimalToCoin(user.getString("balance_btc").toBigDecimal()).toPlainString()
        ltc.text = CoinFormat.decimalToCoin(user.getString("balance_ltc").toBigDecimal()).toPlainString()
        eth.text = CoinFormat.decimalToCoin(user.getString("balance_eth").toBigDecimal()).toPlainString()
        doge.text = CoinFormat.decimalToCoin(user.getString("balance_doge").toBigDecimal()).toPlainString()
      }
    }
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