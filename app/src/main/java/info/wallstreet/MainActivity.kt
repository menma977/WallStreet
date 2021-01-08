package info.wallstreet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.controller.GetController
import info.wallstreet.model.User
import info.wallstreet.view.LoginActivity
import info.wallstreet.view.NavigationActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
  private lateinit var move: Intent
  private lateinit var user: User

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    user = User(this)

    get()
  }

  private fun get() {
    Timer().schedule(1000) {
      val result = GetController("version").call()

      if (result.getInt("code") == 200) {
        setupPreference()
        if (result.getJSONObject("data").getInt("version") != BuildConfig.VERSION_CODE) {
          onMove(token = false, update = true, mt = false, error = false)
        } else if (result.getJSONObject("data").getInt("maintenance") == 1) {
          onMove(token = false, update = false, mt = true, error = false)
        } else {
          if (user.getString("token").isNotEmpty()) {
            onMove(token = true, update = false, mt = false, error = false)
          } else {
            onMove(token = false, update = false, mt = false, error = false)
          }
        }
      } else {
        onMove(token = false, update = false, mt = false, error = true)
      }
    }
  }

  private fun onMove(token: Boolean, update: Boolean, mt: Boolean, error: Boolean) {
    when {
      token -> {
        move = Intent(applicationContext, NavigationActivity::class.java)
        if (user.getString("targetValue").isEmpty()) {
          user.setString("targetValue", "0")
        }
        if (user.getString("progressValue").isEmpty()) {
          user.setString("progressValue", "0")
        }
        if (user.getString("totalMember").isEmpty()) {
          user.setString("totalMember", "-")
        }
        if (user.getString("totalDollar").isEmpty()) {
          user.setString("totalDollar", "-")
        }
        if (user.getString("topSponsor").isEmpty()) {
          user.setString("topSponsor", "-")
        }
      }
      update -> {
        user.clear()
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("update", true)
        move.putExtra("mt", false)
        move.putExtra("lock", false)
      }
      mt -> {
        user.clear()
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("update", false)
        move.putExtra("mt", true)
        move.putExtra("lock", false)
      }
      error -> {
        user.clear()
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("update", false)
        move.putExtra("mt", false)
        move.putExtra("lock", true)
      }
      else -> {
        user.clear()
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("update", false)
        move.putExtra("mt", false)
        move.putExtra("lock", false)
      }
    }

    startActivity(move)
    finishAffinity()
  }

  private fun setupPreference() {
    val result = GetController("upgrade.packages").call()
    if (result.getInt("code") == 200) {
      val pref = getSharedPreferences("option-cached", MODE_PRIVATE).edit()
      val packages = result.getJSONObject("data").getJSONArray("packages")
      pref.putString(
        "packages-json", packages.toString().replace(Regex("\\s+", RegexOption.MULTILINE), "")
      )
      pref.apply()
    }
  }
}