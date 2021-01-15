package info.wallstreet.view.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.history.FakeBalanceActivity
import info.wallstreet.view.history.HistoryBalanceActivity
import info.wallstreet.view.history.UpgradeHistoryActivity
import info.wallstreet.view.modal.UpgradePop
import info.wallstreet.view.modal.WalletQR
import java.util.*
import kotlin.concurrent.schedule

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
  private lateinit var btcPrice: TextView
  private lateinit var ltcPrice: TextView
  private lateinit var ethPrice: TextView
  private lateinit var dogePrice: TextView
  private lateinit var camelPrice: TextView
  private lateinit var tronPrice: TextView
  private lateinit var btcIcon: ImageView
  private lateinit var ltcIcon: ImageView
  private lateinit var ethIcon: ImageView
  private lateinit var dogeIcon: ImageView
  private lateinit var camelIcon: ImageView
  private lateinit var tronIcon: ImageView
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

    btcPrice = view.findViewById(R.id.textViewBtcPrice)
    ltcPrice = view.findViewById(R.id.textViewLtcPrice)
    ethPrice = view.findViewById(R.id.textViewEthPrice)
    dogePrice = view.findViewById(R.id.textViewDogePrice)
    camelPrice = view.findViewById(R.id.textViewCamelPrice)
    tronPrice = view.findViewById(R.id.textViewTronPrice)

    btcIcon = view.findViewById(R.id.iconBTC)
    ltcIcon = view.findViewById(R.id.iconLTC)
    ethIcon = view.findViewById(R.id.iconETH)
    dogeIcon = view.findViewById(R.id.iconDoge)
    camelIcon = view.findViewById(R.id.iconCamel)
    tronIcon = view.findViewById(R.id.iconTron)

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

    renderPriceList()

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
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverPriceList, IntentFilter("web.price.list"))
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
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverPriceList)
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
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverPriceList)
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
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverPriceList)
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
  private var broadcastReceiverPriceList: BroadcastReceiver = object : BroadcastReceiver() {
    @SuppressLint("SetTextI18n")
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("logout")) {
        if (!onLogoutReady) {
          onLogoutReady = true
          parentActivity.onLogout()
        }
      } else {
        val oldBTC = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("old_price_btc").toBigDecimal()))
        val oldLTC = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("old_price_ltc").toBigDecimal()))
        val oldETH = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("old_price_eth").toBigDecimal()))
        val oldDOGE = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("old_price_doge").toBigDecimal()))
        val oldCAMEL = user.getString("old_price_camel").toBigDecimal()
        val oldTRON = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("old_price_tron").toBigDecimal()))
        val newBTC = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("btc_price").toBigDecimal()))
        val newLTC = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("ltc_price").toBigDecimal()))
        val newETH = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("eth_price").toBigDecimal()))
        val newDOGE = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("doge_price").toBigDecimal()))
        val newCAMEL = user.getString("camel_price").toBigDecimal()
        val newTRON = CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(user.getString("tron_price").toBigDecimal()))

        btcPrice.text = "$ " + newBTC.toPlainString()
        user.setString("old_price_btc", user.getString("btc_price"))
        when {
          oldBTC > newBTC -> {
            btcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            btcIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            btcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldBTC < newBTC -> {
            btcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            btcIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            btcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            btcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            btcIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            btcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }

        ltcPrice.text = "$ " + newLTC.toPlainString()
        user.setString("old_price_ltc", user.getString("ltc_price"))
        when {
          oldLTC > newLTC -> {
            ltcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            ltcIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            ltcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldLTC < newLTC -> {
            ltcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            ltcIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            ltcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            ltcPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            ltcIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            ltcIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }

        ethPrice.text = "$ " + newETH.toPlainString()
        user.setString("old_price_eth", user.getString("eth_price"))
        when {
          oldETH > newETH -> {
            ethPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            ethIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            ethIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldETH < newETH -> {
            ethPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            ethIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            ethIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            ethPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            ethIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            ethIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }

        dogePrice.text = "$ " + newDOGE.toPlainString()
        user.setString("old_price_doge", user.getString("doge_price"))
        when {
          oldDOGE > newDOGE -> {
            dogePrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            dogeIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            dogeIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldDOGE < newDOGE -> {
            dogePrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            dogeIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            dogeIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            dogePrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            dogeIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            dogeIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }

        camelPrice.text = "$ " + newCAMEL.toPlainString()
        user.setString("old_price_camel", user.getString("camel_price"))
        when {
          oldCAMEL > newCAMEL -> {
            camelPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            camelIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            camelIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldCAMEL < newCAMEL -> {
            camelPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            camelIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            camelIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            camelPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            camelIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            camelIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }

        tronPrice.text = "$ " + newTRON.toPlainString()
        user.setString("old_price_tron", user.getString("tron_price"))
        when {
          oldTRON > newTRON -> {
            tronPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger))
            tronIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down)
            tronIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Danger), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          oldTRON < newTRON -> {
            tronPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success))
            tronIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_up)
            tronIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Success), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
          else -> {
            tronPrice.setTextColor(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark))
            tronIcon.setImageResource(R.drawable.ic_baseline_arrow_left)
            tronIcon.setColorFilter(ContextCompat.getColor(parentActivity.applicationContext, R.color.Dark), android.graphics.PorterDuff.Mode.MULTIPLY)
          }
        }
      }
    }
  }

  private fun renderPriceList() {
    Timer().schedule(100) {
      val result = GetController("upgrade.price", user.getString("token")).call()
      when {
        result.getInt("code") == 200 -> {
          parentActivity.runOnUiThread {
            btcPrice.text = "$ " + CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(result.getJSONObject("data").getString("btc").toBigDecimal())).toPlainString()
            ltcPrice.text = "$ " + CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(result.getJSONObject("data").getString("ltc").toBigDecimal())).toPlainString()
            ethPrice.text = "$ " + CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(result.getJSONObject("data").getString("eth").toBigDecimal())).toPlainString()
            dogePrice.text = "$ " + CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(result.getJSONObject("data").getString("doge").toBigDecimal())).toPlainString()
            camelPrice.text = "$ " + result.getJSONObject("data").getString("camel")
            tronPrice.text = "$ " + CoinFormat.decimalToCoin(CoinFormat.coinToDecimal(result.getJSONObject("data").getString("tron").toBigDecimal())).toPlainString()

            user.setString("old_price_btc", result.getJSONObject("data").getString("btc"))
            user.setString("old_price_ltc", result.getJSONObject("data").getString("ltc"))
            user.setString("old_price_eth", result.getJSONObject("data").getString("eth"))
            user.setString("old_price_doge", result.getJSONObject("data").getString("doge"))
            user.setString("old_price_camel", result.getJSONObject("data").getString("camel"))
            user.setString("old_price_tron", result.getJSONObject("data").getString("tron"))
          }
        }
        else -> {
          parentActivity.runOnUiThread {
            Toast.makeText(parentActivity.applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          }
        }
      }
    }
  }
}