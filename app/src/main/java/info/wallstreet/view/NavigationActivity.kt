package info.wallstreet.view

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import info.wallstreet.MainActivity
import info.wallstreet.R
import info.wallstreet.background.*
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import info.wallstreet.view.fragment.HomeFragment
import info.wallstreet.view.fragment.SettingFragment
import info.wallstreet.view.user.RegisteredActivity
import java.util.*
import kotlin.concurrent.schedule

class NavigationActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var receiverUpgrade: Intent
  private lateinit var receiverBalances: Intent
  private lateinit var receiverBtc: Intent
  private lateinit var receiverDoge: Intent
  private lateinit var receiverltc: Intent
  private lateinit var receiverEth: Intent
  private lateinit var username: TextView
  private lateinit var homeButton: LinearLayout
  private lateinit var addUserButton: LinearLayout
  private lateinit var settingButton: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)

    user = User(this)
    loading = Loading(this)

    username = findViewById(R.id.textViewUsername)
    homeButton = findViewById(R.id.linearLayoutHome)
    addUserButton = findViewById(R.id.linearLayoutAddUser)
    settingButton = findViewById(R.id.linearLayoutSetting)

    username.text = user.getString("username")

    runService()
    navigation()
    val fragment = HomeFragment()
    addFragment(fragment)
  }

  override fun onStart() {
    super.onStart()
    runService()
  }

  override fun onStop() {
    super.onStop()
    stopService(receiverBalances)
    stopService(receiverUpgrade)
    stopService(receiverBtc)
    stopService(receiverDoge)
    stopService(receiverltc)
    stopService(receiverEth)
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount == 1) {
      stopService(receiverBalances)
      stopService(receiverUpgrade)
      stopService(receiverBtc)
      stopService(receiverDoge)
      stopService(receiverltc)
      stopService(receiverEth)
      finishAffinity()
    } else {
      super.onBackPressed()
    }
  }

  private fun navigation() {
    homeButton.setOnClickListener {
      val fragment = HomeFragment()
      addFragment(fragment)
    }

    addUserButton.setOnClickListener {
      val move = Intent(this, RegisteredActivity::class.java)
      startActivity(move)
    }

    settingButton.setOnClickListener {
      val fragment = SettingFragment()
      addFragment(fragment)
    }
  }

  private fun runService() {
    Timer().schedule(1000) {
      receiverBalances = Intent(applicationContext, BalanceService::class.java)
      receiverUpgrade = Intent(applicationContext, UpgradeService::class.java)
      receiverBtc = Intent(applicationContext, BtcService::class.java)
      receiverDoge = Intent(applicationContext, DogeService::class.java)
      receiverltc = Intent(applicationContext, LtcService::class.java)
      receiverEth = Intent(applicationContext, EthService::class.java)

      startService(receiverBalances)
      startService(receiverUpgrade)
      startService(receiverBtc)
      startService(receiverDoge)
      startService(receiverltc)
      startService(receiverEth)
    }
  }

  fun onLogout() {
    Timer().schedule(100) {
      GetController("logout", user.getString("token")).call()
      runOnUiThread {
        user.clear()
        finishAffinity()
        val move = Intent(applicationContext, MainActivity::class.java)
        loading.closeDialog()
        startActivity(move)
      }
    }
  }

  private fun addFragment(fragment: Fragment) {
    val backStateName = fragment.javaClass.simpleName
    val fragmentManager = supportFragmentManager
    val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

    if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
      val fragmentTransaction = fragmentManager.beginTransaction()
      fragmentTransaction.replace(R.id.contentFragment, fragment, backStateName)
      fragmentTransaction.addToBackStack(backStateName)
      fragmentTransaction.commit()
    }
  }
}