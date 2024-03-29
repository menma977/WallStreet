package info.wallstreet.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity
import info.wallstreet.view.history.HistoryUpgradeListActivity
import info.wallstreet.view.user.EditPasswordActivity
import java.math.BigDecimal

class SettingFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var username: TextView
  private lateinit var email: TextView
  private lateinit var phone: TextView
  private lateinit var level: TextView
  private lateinit var profit: TextView
  private lateinit var profitDollar: TextView
  private lateinit var totalMember: TextView
  private lateinit var totalDollar: TextView
  private lateinit var topSponsor: TextView
  private lateinit var firstUpgrade: TextView
  private lateinit var historyUpgrade: TextView
  private lateinit var editPassword: TextView
  private lateinit var editSecondaryPassword: TextView
  private lateinit var logout: TextView
  private lateinit var move: Intent

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)

    username = view.findViewById(R.id.textViewUsername)
    email = view.findViewById(R.id.textViewEmail)
    phone = view.findViewById(R.id.textViewPhone)
    level = view.findViewById(R.id.textViewLevel)
    profit = view.findViewById(R.id.textViewShare)
    profitDollar = view.findViewById(R.id.textViewShareDollar)
    totalMember = view.findViewById(R.id.textViewTotalMember)
    totalDollar = view.findViewById(R.id.textViewTotalDollar)
    topSponsor = view.findViewById(R.id.textViewTopSponsor)
    firstUpgrade = view.findViewById(R.id.textViewFirstUpgrade)
    historyUpgrade = view.findViewById(R.id.textViewHistoryUpgrade)
    editPassword = view.findViewById(R.id.textViewEditPassword)
    editSecondaryPassword = view.findViewById(R.id.textViewEditSecondaryPassword)
    logout = view.findViewById(R.id.textViewLogout)

    username.text = user.getString("username")
    email.text = user.getString("email")
    phone.text = user.getString("phone")
    level.text =
        "Current TopUp : $${CoinFormat.toDollar(user.getString("targetValue").toBigDecimal() / BigDecimal(3)).toPlainString()}"
    profit.text =
        "Random Share Camel : ${CoinFormat.decimalToCoin(user.getString("profit").toBigDecimal()).toPlainString()}"
    profitDollar.text =
        "Random Share Dollar : ${CoinFormat.toDollar(user.getString("profitDollar").toBigDecimal()).toPlainString()}"
    firstUpgrade.text =
        "First Upgrade : \$${user.getString("firstUpgradeValue")} at ${user.getString("firstUpgradeDate")}"
    totalMember.text = user.getString("totalMember")
    totalDollar.text = user.getString("totalDollar")
    topSponsor.text = user.getString("topSponsor")

    historyUpgrade.setOnClickListener {
      move = Intent(parentActivity, HistoryUpgradeListActivity::class.java)
      startActivity(move)
    }

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

    logout.setOnClickListener { parentActivity.onLogout() }

    return view
  }
}
