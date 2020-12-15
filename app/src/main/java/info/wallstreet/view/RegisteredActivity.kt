package info.wallstreet.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import info.wallstreet.R
import info.wallstreet.controller.PostController
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class RegisteredActivity : RegistrationActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insideRegistration = true

        val toLoginGroup = findViewById<LinearLayout>(R.id.toLoginGroup)
        toLoginGroup.visibility = View.GONE
        sponsor.visibility = View.GONE
        password.visibility = View.GONE
        password_confirm.visibility = View.GONE
        second_password.visibility = View.GONE
        second_password_confirm.visibility = View.GONE
    }

    override fun doRegister(){
        val body = FormBody.Builder()
        body.addEncoded("sponsor", user.getString("username"))
        body.addEncoded("name", name.text.toString())
        body.addEncoded("username", username.text.toString())
        body.addEncoded("email", email.text.toString())
        body.addEncoded("phone", phone.text.toString())
        body.addEncoded("password", "password123")
        body.addEncoded("confirmation_password", "password123")
        body.addEncoded("secondary_password", "123456")
        body.addEncoded("confirmation_secondary_password", "123456")
        Timer().schedule(1000) {
            val response = PostController("register", body).call()
            if (response.getInt("code") == 200){
                runOnUiThread {
                    Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_LONG).show()
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