package info.wallstreet.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.PostController
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

open class RegistrationActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var sponsor: EditText
  private lateinit var username: EditText
  private lateinit var name: EditText
  private lateinit var email: EditText
  private lateinit var phone: EditText
  private lateinit var password: EditText
  private lateinit var confirmPassword: EditText
  private lateinit var secondaryPassword: EditText
  private lateinit var confirmSecondaryPassword: EditText
  private lateinit var register: Button
  private lateinit var login: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    loading = Loading(this)

    sponsor = findViewById(R.id.editTextSponsor)
    username = findViewById(R.id.editTextUsername)
    name = findViewById(R.id.editTextName)
    email = findViewById(R.id.editTextEmail)
    phone = findViewById(R.id.editTextPhone)
    password = findViewById(R.id.editTextPassword)
    confirmPassword = findViewById(R.id.editTextConfirmationPassword)
    secondaryPassword = findViewById(R.id.editTextSecondaryPassword)
    confirmSecondaryPassword = findViewById(R.id.editTextConfirmationSecondaryPassword)
    register = findViewById(R.id.buttonRegister)
    login = findViewById(R.id.textVIewLogin)

    register.setOnClickListener {
      validation()
    }

    login.setOnClickListener {
      finish()
    }
  }

  private fun validation() {
    loading.openDialog()
    when {
      sponsor.text.isEmpty() -> {
        Toast.makeText(this, "sponsor required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        sponsor.requestFocus()
      }
      name.text.isEmpty() -> {
        Toast.makeText(this, "name required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        name.requestFocus()
      }
      username.text.isEmpty() -> {
        Toast.makeText(this, "username required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        username.requestFocus()
      }
      email.text.isEmpty() -> {
        Toast.makeText(this, "email required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        email.requestFocus()
      }
      phone.text.isEmpty() -> {
        Toast.makeText(this, "phone required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        phone.requestFocus()
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
        onRegistration()
      }
    }
  }

  private fun onRegistration() {
    val body = FormBody.Builder()
    body.addEncoded("sponsor", sponsor.text.toString())
    body.addEncoded("name", name.text.toString())
    body.addEncoded("username", username.text.toString())
    body.addEncoded("email", email.text.toString())
    body.addEncoded("phone", phone.text.toString())
    body.addEncoded("password", password.text.toString())
    body.addEncoded("confirmation_password", confirmPassword.text.toString())
    body.addEncoded("secondary_password", secondaryPassword.text.toString())
    body.addEncoded("confirmation_secondary_password", confirmSecondaryPassword.text.toString())
    Timer().schedule(1000) {
      val result = PostController("registration", body).call()

      Log.i("json", result.toString())

      if (result.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          finish()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }
}