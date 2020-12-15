package info.wallstreet.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule
import kotlin.properties.Delegates

open class RegistrationActivity : AppCompatActivity() {
  private lateinit var login: Intent

  protected lateinit var sponsor: EditText
  protected lateinit var password: EditText
  protected lateinit var password_confirm: EditText
  protected lateinit var second_password: EditText
  protected lateinit var second_password_confirm: EditText

  protected lateinit var username: EditText
  protected lateinit var name: EditText
  protected lateinit var email: EditText
  protected lateinit var phone: EditText

  protected lateinit var user: User
  protected lateinit var loading: Loading

  protected var insideRegistration = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    user = User(this)
    loading = Loading(this)

    sponsor = findViewById(R.id.editTextSponsor)
    username = findViewById(R.id.editTextUsername)
    name = findViewById(R.id.editTextName)
    email = findViewById(R.id.editTextEmail)
    phone = findViewById(R.id.editTextPhone)
    password = findViewById(R.id.editTextPassword)
    password_confirm = findViewById(R.id.editTextConfirmationPassword)
    second_password = findViewById(R.id.editTextSecondaryPassword)
    second_password_confirm = findViewById(R.id.editTextConfirmationSecondaryPassword)

    val registerBtn = findViewById<Button>(R.id.buttonRegister)
    val toLogin = findViewById<TextView>(R.id.textVIewLogin)

    login = Intent(applicationContext, LoginActivity::class.java)

    registerBtn.setOnClickListener(View.OnClickListener {
      loading.openDialog()
      val validity = validation()
      if(validity.contentEquals(""))
        doRegister()
      else {
        loading.closeDialog()
        Toast.makeText(this, validity, Toast.LENGTH_SHORT).show()
      }
    })

    toLogin.setOnClickListener {
      startActivity(login)
      finishAffinity()
    }
  }

  private fun validation(): String {
    if(!insideRegistration){
      if(TextUtils.isEmpty(sponsor.text.toString()))
        return "Sponsor cannot be empty"
      if(TextUtils.isEmpty(password.text.toString()) || second_password.text.toString().length < 10)
        return "Password at least have 10 characters"
      if(TextUtils.isEmpty(second_password.text.toString()) || second_password.text.toString().length < 6)
        return "Secondary Password needs to be 6 or more digits/numbers"
      if(TextUtils.equals(password.text.toString(), password_confirm.text.toString()))
        return "Password confirmation didn't match"
      if(TextUtils.equals(second_password.text.toString(), second_password_confirm.text.toString()))
        return "Secondary Password confirmation didn't match"
    }
    if(TextUtils.isEmpty(username.text.toString()))
      return "Username cannot be empty"
    if(TextUtils.isEmpty(name.text.toString()))
      return "Name cannot be empty"
    if(TextUtils.isEmpty(email.text.toString()) || !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches())
      return "Email is empty or have incorrect format"
    if(TextUtils.isEmpty(phone.text.toString()) || !Patterns.PHONE.matcher(phone.text.toString()).matches())
      return "Phone is empty or have incorrect format"
    return ""
  }

  protected open fun doRegister(){
    val body = FormBody.Builder()
    body.addEncoded("sponsor", sponsor.text.toString())
    body.addEncoded("name", name.text.toString())
    body.addEncoded("username", username.text.toString())
    body.addEncoded("email", email.text.toString())
    body.addEncoded("phone", phone.text.toString())
    body.addEncoded("password", password.text.toString())
    body.addEncoded("confirmation_password", password_confirm.text.toString())
    body.addEncoded("secondary_password", second_password.text.toString())
    body.addEncoded("confirmation_secondary_password", second_password_confirm.text.toString())
    Timer().schedule(1000) {
      val response = PostController("register", body).call()
      if (response.getInt("code") == 200){
        runOnUiThread {
          Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_LONG).show()
          startActivity(login)
          finishAffinity()
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }
}