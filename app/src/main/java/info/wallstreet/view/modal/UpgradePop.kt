package info.wallstreet.view.modal

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import info.wallstreet.R
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import okhttp3.FormBody

class UpgradePop constructor(context: Context, user: User) : AlertDialog(context) {
  init {
    val layout: LayoutInflater =
      context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = layout.inflate(R.layout.modal_upgrade_layout, LinearLayout(context), false)
    setView(view)
    val typeCurrency = view.findViewById<Spinner>(R.id.currency)
    val typePackages = view.findViewById<Spinner>(R.id.packages)
    val secondaryPassword = view.findViewById<EditText>(R.id.secondary_password)
    (view.findViewById<ImageView>(R.id.closeBtn)).setOnClickListener {
      dismiss()
    }
    (view.findViewById<Button>(R.id.upgradeBtn)).setOnClickListener {
      val type = when (typeCurrency.selectedItem) {
        "Bitcoin" -> "btc"
        "Litecoin" -> "ltc"
        "Ethereum" -> "eth"
        "Dogecoin" -> "doge"
        else -> "btc"
      }
      val pkg = typePackages.selectedItem.toString()
      val pass = secondaryPassword.text.toString()
      val balance = user.getString("balance_$type")
      val body = FormBody.Builder()
      body.add("type", type)
      body.add("upgrade_list", pkg)
      body.add("balance", balance)
      body.add("secondaryPassword", pass)
      val response = PostController("upgrade", body).call()
      if (response.getInt("code") != 500) {
        Toast.makeText(context, response.getString("data"), Toast.LENGTH_LONG).show()
        dismiss()
      } else {
        Toast.makeText(context, response.getString("data"), Toast.LENGTH_LONG).show()
      }
      dismiss()
    }
  }
}