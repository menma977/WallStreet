package info.wallstreet.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.config.Loading
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.history.FakeBalanceActivity
import info.wallstreet.view.history.HistoryBalanceActivity
import info.wallstreet.view.history.UpgradeHistoryActivity
import info.wallstreet.view.modal.UpgradePop
import info.wallstreet.view.modal.WalletQR

class HomeFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var loading: Loading
  private lateinit var user: User
  private lateinit var btc: TextView
  private lateinit var btcFake: TextView
  private lateinit var ltc: TextView
  private lateinit var ltcFake: TextView
  private lateinit var eth: TextView
  private lateinit var ethFake: TextView
  private lateinit var doge: TextView
  private lateinit var dogeFake: TextView
  private lateinit var camel: TextView
  private lateinit var camelFake: TextView
  private lateinit var tron: TextView
  private lateinit var toBTCWallet: ImageView
  private lateinit var toLTCWallet: ImageView
  private lateinit var toETHWallet: ImageView
  private lateinit var toDOGEWallet: ImageView
  private lateinit var toCamelWallet: ImageView
  private lateinit var progressBar: ProgressBar
  private lateinit var progressValue: TextView
  private lateinit var targetValue: TextView
  private lateinit var upgradeBtn: LinearLayout
  private lateinit var historyUpgradeButton: LinearLayout
  private lateinit var historyWall: LinearLayout
  private lateinit var history999: LinearLayout
  private lateinit var move: Intent
  private var onLogoutReady = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    btc = view.findViewById(R.id.textViewBitcoinBalance)
    btcFake = view.findViewById(R.id.textViewBitcoinBalanceFake)
    ltc = view.findViewById(R.id.textViewLiteCoinBalance)
    ltcFake = view.findViewById(R.id.textViewLiteCoinBalanceFake)
    eth = view.findViewById(R.id.textViewEthereumBalance)
    ethFake = view.findViewById(R.id.textViewEthereumBalanceFake)
    doge = view.findViewById(R.id.textViewDogeCoinBalance)
    dogeFake = view.findViewById(R.id.textViewDogeCoinBalanceFake)
    camel = view.findViewById(R.id.textViewCamelBalance)
    camelFake = view.findViewById(R.id.textViewCamelWallBalance)
    tron = view.findViewById(R.id.textViewTronBalance)

    toBTCWallet = view.findViewById(R.id.toBtcWallet)
    toLTCWallet = view.findViewById(R.id.toLtcWallet)
    toETHWallet = view.findViewById(R.id.toEthWallet)
    toDOGEWallet = view.findViewById(R.id.toDogeWallet)
    toCamelWallet = view.findViewById(R.id.toCamelWallet)

    upgradeBtn = view.findViewById(R.id.buttonUpgrade)
    historyUpgradeButton = view.findViewById(R.id.history_upgrades)
    historyWall = view.findViewById(R.id.buttonHistoryWall)
    history999 = view.findViewById(R.id.buttonHistory999)

    progressBar = view.findViewById(R.id.progressBar)
    progressValue = view.findViewById(R.id.textViewProgressBar)
    targetValue = view.findViewById(R.id.textViewTarget)

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

    history999.setOnClickListener {
      move = Intent(parentActivity, HistoryBalanceActivity::class.java)
      startActivity(move)
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

    toCamelWallet.setOnClickListener {
      WalletQR.show(parentActivity, "camel", user)
    }

    progressValue.text = "$ ${CoinFormat.toDollar(user.getString("progressValue").toBigDecimal()).toPlainString()}"
    targetValue.text = "$ ${CoinFormat.toDollar(user.getString("targetValue").toBigDecimal()).toPlainString()}"

    upgradeBtn.isEnabled = !user.getBoolean("on_queue")

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

    if (user.getString("balance_camel").isNotEmpty()) {
      camel.text = user.getString("balance_camel")
    } else {
      camel.text = "0"
    }

    if (user.getString("balance_tron").isNotEmpty()) {
      tron.text = user.getString("balance_tron")
    } else {
      tron.text = "0"
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

    if (user.getString("fake_balance_camel").isNotEmpty()) {
      camelFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_camel").toBigDecimal()).toPlainString()
    } else {
      camelFake.text = "0"
    }
  }

  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverBalances, IntentFilter("doge.balances"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverUpgrade, IntentFilter("web.upgrade"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverBtc, IntentFilter("web.btc"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverDoge, IntentFilter("web.doge"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverEth, IntentFilter("web.eth"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverLtc, IntentFilter("web.ltc"))
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverCamel, IntentFilter("web.camel"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverCamel)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverCamel)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBalances)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverUpgrade)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverBtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDoge)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverEth)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverLtc)
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverCamel)
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
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
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
        camel.text = user.getString("balance_camel")
        tron.text = user.getString("balance_tron")
      }
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
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
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
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
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
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
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
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
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
    }
  }
  private var broadcastReceiverCamel: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        camelFake.text = CoinFormat.decimalToCoin(user.getString("fake_balance_camel").toBigDecimal()).toPlainString()
      }
      upgradeBtn.isEnabled = !user.getBoolean("on_queue")
    }
  }
}