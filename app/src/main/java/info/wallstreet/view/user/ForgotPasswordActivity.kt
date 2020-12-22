package info.wallstreet.view.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.PostController
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ForgotPasswordActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var email: EditText
  private lateinit var send: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forgot_password)

    loading = Loading(this)

    email = findViewById(R.id.editTextEmail)
    send = findViewById(R.id.buttonSend)

    send.setOnClickListener {
      loading.openDialog()
      if (email.text.isEmpty()) {
        Toast.makeText(this, "email required", Toast.LENGTH_SHORT).show()
        loading.closeDialog()
        email.requestFocus()
      } else {
        val body = FormBody.Builder()
        body.addEncoded("email", email.text.toString())
        Timer().schedule(1000) {
          val result = PostController("forgot-password", body).call()

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
  }
}