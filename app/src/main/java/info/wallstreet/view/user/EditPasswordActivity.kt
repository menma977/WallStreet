package info.wallstreet.view.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import info.wallstreet.MainActivity
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class EditPasswordActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var containerUpdatePassword: LinearLayout
  private lateinit var secondaryPasswordRequired: EditText
  private lateinit var password: EditText
  private lateinit var confirmPassword: EditText
  private lateinit var updatePassword: Button
  private lateinit var containerUpdateSecondaryPassword: LinearLayout
  private lateinit var secondaryPassword: EditText
  private lateinit var confirmSecondaryPassword: EditText
  private lateinit var updateSecondaryPassword: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_password)

    user = User(this)
    loading = Loading(this)

    containerUpdatePassword = findViewById(R.id.containerUpdatePassword)
    secondaryPasswordRequired = findViewById(R.id.editTextSecondaryPasswordRequired)
    password = findViewById(R.id.editTextPassword)
    confirmPassword = findViewById(R.id.editTextConfirmationPassword)
    updatePassword = findViewById(R.id.buttonUpdatePassword)
    containerUpdateSecondaryPassword = findViewById(R.id.containerUpdateSecondaryPassword)
    secondaryPassword = findViewById(R.id.editTextSecondaryPassword)
    confirmSecondaryPassword = findViewById(R.id.editTextConfirmationSecondaryPassword)
    updateSecondaryPassword = findViewById(R.id.buttonUpdateSecondaryPassword)
    val isPassword = intent.getBooleanExtra("isPassword", false)

    if (isPassword) {
      containerUpdatePassword.visibility = LinearLayout.VISIBLE
      containerUpdateSecondaryPassword.visibility = LinearLayout.GONE
    } else {
      containerUpdatePassword.visibility = LinearLayout.GONE
      containerUpdateSecondaryPassword.visibility = LinearLayout.VISIBLE
    }

    updatePassword.setOnClickListener {
      loading.openDialog()
      when {
        secondaryPasswordRequired.text.isEmpty() -> {
          Toast.makeText(this, "secondary password required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          secondaryPasswordRequired.requestFocus()
        }
        password.text.isEmpty() -> {
          Toast.makeText(this, "password required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          password.requestFocus()
        }
        confirmPassword.text.isEmpty() -> {
          Toast.makeText(this, "confirmation password required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          confirmPassword.requestFocus()
        }
        else -> {
          updatePasswordExecution()
        }
      }
    }

    updateSecondaryPassword.setOnClickListener {
      loading.openDialog()
      when {
        secondaryPassword.text.isEmpty() -> {
          Toast.makeText(this, "secondary password required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          secondaryPassword.requestFocus()
        }
        confirmSecondaryPassword.text.isEmpty() -> {
          Toast.makeText(this, "confirmation secondary password required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          confirmSecondaryPassword.requestFocus()
        }
        else -> {
          updateSecondaryPasswordExecution()
        }
      }
    }
  }

  private fun updatePasswordExecution() {
    val body = FormBody.Builder()
    body.addEncoded("secondary_password", secondaryPasswordRequired.text.toString())
    body.addEncoded("password", password.text.toString())
    body.addEncoded("confirmation_password", confirmPassword.text.toString())
    Timer().schedule(100) {
      val result = PostController("user.update", user.getString("token"), body).call()
      when {
        result.getInt("code") == 200 -> {
          runOnUiThread {
            Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
            finish()
          }
        }
        result.getBoolean("logout") -> {
          runOnUiThread {
            onLogout()
          }
        }
        else -> {
          runOnUiThread {
            Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
            loading.closeDialog()
          }
        }
      }
    }
  }

  private fun updateSecondaryPasswordExecution() {
    val body = FormBody.Builder()
    body.addEncoded("secondary_password", secondaryPassword.text.toString())
    body.addEncoded("confirmation_secondary_password", confirmSecondaryPassword.text.toString())
    Timer().schedule(100) {
      val result = PostController("user.update", user.getString("token"), body).call()
      when {
        result.getInt("code") == 200 -> {
          runOnUiThread {
            Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
            finish()
          }
        }
        result.getBoolean("logout") -> {
          runOnUiThread {
            onLogout()
          }
        }
        else -> {
          runOnUiThread {
            Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
            loading.closeDialog()
          }
        }
      }
    }
  }

  private fun onLogout() {
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
}