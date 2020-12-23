package info.wallstreet.view.modal

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.R
import info.wallstreet.controller.PostController
import info.wallstreet.model.User
import okhttp3.FormBody
import org.json.JSONArray
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.schedule

class UpgradePop constructor(context: Context, private val user: User) : AlertDialog(context) {
  private val typeCurrency: Spinner
  private val typePackages: Spinner
  private val secondaryPassword: EditText
  private val packages: LinkedHashMap<String, Int>

  init {
    val layout: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = layout.inflate(R.layout.modal_upgrade_layout, LinearLayout(context), false)
    if (context is Activity) setOwnerActivity(context)
    setView(view)
    typeCurrency = view.findViewById(R.id.currency)
    typePackages = view.findViewById(R.id.packages)
    secondaryPassword = view.findViewById(R.id.secondary_password)
    packages = LinkedHashMap()
    populatePackages()

    (view.findViewById<ImageView>(R.id.closeBtn)).setOnClickListener {
      dismiss()
    }
    (view.findViewById<Button>(R.id.upgradeBtn)).setOnClickListener {
      upgrade()
    }
  }

  private fun populatePackages() {
    val pref = context.getSharedPreferences("option-cached", AppCompatActivity.MODE_PRIVATE)
    if (pref.contains("packages-json")) {
      val packagesJSON = JSONArray(pref.getString("packages-json", "[]"))
      for (i in 0 until packagesJSON.length()) {
        val pkg = packagesJSON.getJSONObject(i)
        packages[pkg.getString("dollar")] = pkg.getInt("id")
      }
      val keys = Array<String>(packages.size) { "" }
      var i = 0
      packages.forEach { (k, v) ->
        keys[i++] = k
      }
      typePackages.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, keys)
    } else {
      dismiss()
      Toast.makeText(
        context, "cannot find packages, please restart application", Toast.LENGTH_SHORT
      ).show()
    }
  }

  private fun upgrade() {
    val type = when (typeCurrency.selectedItem) {
      "Bitcoin" -> "btc"
      "Litecoin" -> "ltc"
      "Ethereum" -> "eth"
      "Dogecoin" -> "doge"
      else -> "btc"
    }
    val softkey = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    softkey.hideSoftInputFromWindow(View(ownerActivity).windowToken, 0)
    val pkg = typePackages.selectedItem.toString()
    val pass = secondaryPassword.text.toString()
    val balance = user.getString("balance_$type")
    val body = FormBody.Builder()
    body.add("type", type)
    body.add("upgrade_list", pkg)
    body.add("balance", balance)
    body.add("secondary_password", pass)
    Timer().schedule(100) {
      val response = PostController("upgrade.store", user.getString("token"), body).call()
      ownerActivity?.runOnUiThread {
        Toast.makeText(context, response.getString("data"), Toast.LENGTH_LONG).show()
        if (response.getInt("code") != 500) dismiss()
      }
    }
  }
}