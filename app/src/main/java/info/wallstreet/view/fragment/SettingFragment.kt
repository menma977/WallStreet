package info.wallstreet.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.wallstreet.R
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.user.EditPasswordActivity

class SettingFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var username: TextView
  private lateinit var email: TextView
  private lateinit var phone: TextView
  private lateinit var level: TextView
  private lateinit var editPassword: TextView
  private lateinit var editSecondaryPassword: TextView
  private lateinit var logout: TextView
  private lateinit var move: Intent

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)

    username = view.findViewById(R.id.textViewUsername)
    email = view.findViewById(R.id.textViewEmail)
    phone = view.findViewById(R.id.textViewPhone)
    level = view.findViewById(R.id.textViewLevel)
    editPassword = view.findViewById(R.id.textViewEditPassword)
    editSecondaryPassword = view.findViewById(R.id.textViewEditSecondaryPassword)
    logout = view.findViewById(R.id.textViewLogout)

    username.text = user.getString("username")
    email.text = user.getString("email")
    phone.text = user.getString("phone")
    level.text = "Current LEVEL : ${user.getString("level")}"

    editPassword.setOnClickListener {
      move = Intent(parentActivity, EditPasswordActivity::class.java)
      move.putExtra("isPassword", true)
      startActivity(move)
    }

    editSecondaryPassword.setOnClickListener {
      move = Intent(parentActivity, EditPasswordActivity::class.java)
      move.putExtra("isPassword", false)
      startActivity(move)
    }

    logout.setOnClickListener {
      parentActivity.onLogout()
    }

    return view
  }
}