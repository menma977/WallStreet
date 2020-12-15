package info.wallstreet.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import info.wallstreet.BuildConfig
import info.wallstreet.MainActivity
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.PostController
import info.wallstreet.model.Url
import info.wallstreet.model.User
import info.wallstreet.view.user.ForgotPasswordActivity
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var move: Intent
  private lateinit var description: TextView
  private lateinit var version: TextView
  private lateinit var textViewRegister: TextView
  private lateinit var textViewForgotPassword: TextView
  private lateinit var username: EditText
  private lateinit var password: EditText
  private lateinit var loginButton: Button
  private lateinit var updateButton: Button
  private lateinit var reloadButton: Button
  private lateinit var containerRegister: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    user = User(this)
    loading = Loading(this)

    description = findViewById(R.id.textViewDescription)
    version = findViewById(R.id.textViewVersion)
    textViewRegister = findViewById(R.id.textViewRegister)
    textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
    username = findViewById(R.id.editTextUsername)
    password = findViewById(R.id.editTextPassword)
    loginButton = findViewById(R.id.buttonLogin)
    updateButton = findViewById(R.id.buttonUpdate)
    reloadButton = findViewById(R.id.buttonReloaded)
    containerRegister = findViewById(R.id.linearLayoutContainerRegister)

    username.setText("admin")
    password.setText("admin")

    version.text = BuildConfig.VERSION_NAME

    intentResponseHandler()

    textViewRegister.setOnClickListener {
      move = Intent(this, RegistrationActivity::class.java)
      startActivity(move)
    }

    textViewForgotPassword.setOnClickListener {
      move = Intent(this, ForgotPasswordActivity::class.java)
      startActivity(move)
    }

    updateButton.setOnClickListener {
      user.clear()
      move = Intent(Intent.ACTION_VIEW, Uri.parse(Url.web("").replace("api/", "")))
      startActivity(move)
    }

    reloadButton.setOnClickListener {
      move = Intent(this, MainActivity::class.java)
      startActivity(move)
      finishAffinity()
    }

    loginButton.setOnClickListener {
      loading.openDialog()
      when {
        validatePermission() -> {
          loading.closeDialog()
          doRequestPermission()
        }
        username.text.isEmpty() -> {
          Toast.makeText(this, "username required", Toast.LENGTH_SHORT).show()
          username.requestFocus()
          loading.closeDialog()
        }
        password.text.isEmpty() -> {
          Toast.makeText(this, "password required", Toast.LENGTH_SHORT).show()
          password.requestFocus()
          loading.closeDialog()
        }
        else -> {
          doLogin()
        }
      }
    }
  }

  private fun doLogin() {
    val body = FormBody.Builder()
    body.addEncoded("username", username.text.toString())
    body.addEncoded("password", password.text.toString())
    Timer().schedule(1000) {
      val result = PostController("login", body).call()

      Log.i("json", result.toString())

      if (result.getInt("code") == 200) {
        user.setString("token", result.getJSONObject("data").getString("token"))
        user.setString("cookie", result.getJSONObject("data").getString("cookie"))
        user.setString("email", result.getJSONObject("data").getString("email"))
        user.setString("username", result.getJSONObject("data").getString("username"))
        user.setString("phone", result.getJSONObject("data").getString("phone"))
        user.setString("wallet_btc", result.getJSONObject("data").getString("wallet_btc"))
        user.setString("wallet_doge", result.getJSONObject("data").getString("wallet_doge"))
        user.setString("wallet_ltc", result.getJSONObject("data").getString("wallet_ltc"))
        user.setString("wallet_eth", result.getJSONObject("data").getString("wallet_eth"))
        user.setString("level", result.getJSONObject("data").getString("level"))

        runOnUiThread {
          move = Intent(applicationContext, NavigationActivity::class.java)
          startActivity(move)
          finishAffinity()
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun intentResponseHandler() {
    when {
      intent.getBooleanExtra("update", false) -> {
        description.text = "Your app need update"
        textViewForgotPassword.visibility = TextView.GONE
        containerRegister.visibility = LinearLayout.GONE
        username.visibility = EditText.GONE
        password.visibility = EditText.GONE
        loginButton.visibility = Button.GONE
        reloadButton.visibility = Button.GONE
        updateButton.visibility = Button.VISIBLE
      }
      intent.getBooleanExtra("mt", false) -> {
        description.text = "We are under maintenance"
        textViewForgotPassword.visibility = TextView.GONE
        containerRegister.visibility = LinearLayout.GONE
        username.visibility = EditText.GONE
        password.visibility = EditText.GONE
        loginButton.visibility = Button.GONE
        reloadButton.visibility = Button.GONE
        updateButton.visibility = Button.GONE
      }
      intent.getBooleanExtra("lock", false) -> {
        description.text = "Your connection is not stable"
        textViewForgotPassword.visibility = TextView.GONE
        containerRegister.visibility = LinearLayout.GONE
        username.visibility = EditText.GONE
        password.visibility = EditText.GONE
        loginButton.visibility = Button.GONE
        reloadButton.visibility = Button.VISIBLE
        updateButton.visibility = Button.GONE
      }
      else -> {
        textViewForgotPassword.visibility = TextView.VISIBLE
        containerRegister.visibility = LinearLayout.VISIBLE
        username.visibility = EditText.VISIBLE
        password.visibility = EditText.VISIBLE
        loginButton.visibility = Button.VISIBLE
        reloadButton.visibility = Button.GONE
        updateButton.visibility = Button.GONE
      }
    }
  }

  private fun validatePermission(): Boolean {
    return when {
      ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
        false
      }
      else -> {
        true
      }
    }
  }

  private fun doRequestPermission() {
    requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
  }
}