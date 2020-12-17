package info.wallstreet.view

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import info.wallstreet.MainActivity
import info.wallstreet.R
import info.wallstreet.background.*
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import info.wallstreet.view.fragment.HomeFragment
import java.util.*
import kotlin.concurrent.schedule

class NavigationActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var receiverBalances: Intent
  private lateinit var receiverBtc: Intent
  private lateinit var receiverDoge: Intent
  private lateinit var receiverltc: Intent
  private lateinit var receiverEth: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)

    user = User(this)
    loading = Loading(this)

    runService()
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
    stopService(receiverBtc)
    stopService(receiverDoge)
    stopService(receiverltc)
    stopService(receiverEth)
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount == 1) {
      stopService(receiverBalances)
      stopService(receiverBtc)
      stopService(receiverDoge)
      stopService(receiverltc)
      stopService(receiverEth)
      finishAffinity()
    } else {
      super.onBackPressed()
    }
  }

  private fun runService() {
    Timer().schedule(1000) {
      receiverBalances = Intent(applicationContext, BalanceService::class.java)
      receiverBtc = Intent(applicationContext, BtcService::class.java)
      receiverDoge = Intent(applicationContext, DogeService::class.java)
      receiverltc = Intent(applicationContext, LtcService::class.java)
      receiverEth = Intent(applicationContext, EthService::class.java)

      startService(receiverBalances)
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