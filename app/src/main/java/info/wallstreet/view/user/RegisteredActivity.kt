package info.wallstreet.view.user

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class RegisteredActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var username: EditText
  private lateinit var name: EditText
  private lateinit var email: EditText
  private lateinit var phone: EditText
  private lateinit var register: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registered)

    user = User(this)
    loading = Loading(this)

    username = findViewById(R.id.editTextUsername)
    name = findViewById(R.id.editTextName)
    email = findViewById(R.id.editTextEmail)
    phone = findViewById(R.id.editTextPhone)
    register = findViewById(R.id.buttonRegister)

    register.setOnClickListener {
      validation()
    }
  }

  private fun validation() {
    loading.openDialog()
    when {
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
      else -> {
        onRegistration()
      }
    }
  }

  private fun onRegistration() {
    val body = FormBody.Builder()
    body.addEncoded("name", name.text.toString())
    body.addEncoded("username", username.text.toString())
    body.addEncoded("email", email.text.toString())
    body.addEncoded("phone", phone.text.toString())
    Timer().schedule(1000) {
      val result = PostController("user.registered", user.getString("token"), body).call()

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