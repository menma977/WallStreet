package info.wallstreet.view.modal

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.controller.PostController
import info.wallstreet.model.PackageCls
import info.wallstreet.model.User
import okhttp3.FormBody
import org.json.JSONArray
import java.math.BigDecimal
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.schedule

class UpgradePop constructor(context: Context, private val user: User) : AlertDialog(context) {
  private val typeCurrency: Spinner
  private val typePackages: Spinner
  private val secondaryPassword: EditText
  private val price: EditText
  private val packages: LinkedHashMap<String, PackageCls>

  init {
    val layout: LayoutInflater =
      context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = layout.inflate(R.layout.modal_upgrade_layout, LinearLayout(context), false)
    if (context is Activity) setOwnerActivity(context)
    setView(view)
    typeCurrency = view.findViewById(R.id.currency)
    typePackages = view.findViewById(R.id.packages)
    price = view.findViewById(R.id.price)
    secondaryPassword = view.findViewById(R.id.secondary_password)
    packages = LinkedHashMap()
    populatePackages()

    (view.findViewById<ImageView>(R.id.closeBtn)).setOnClickListener { dismiss() }
    (view.findViewById<Button>(R.id.upgradeBtn)).setOnClickListener { upgrade() }

    typeCurrency.onItemSelectedListener =
      object : OnItemSelectedListener {
        override fun onItemSelected(
          parent: AdapterView<*>?,
          view: View,
          position: Int,
          id: Long
        ) {
          updatePrice()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }

    typePackages.onItemSelectedListener =
      object : OnItemSelectedListener {
        override fun onItemSelected(
          parent: AdapterView<*>?,
          view: View,
          position: Int,
          id: Long
        ) {
          updatePrice()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }
  }

  private fun populatePackages() {
    val pref = context.getSharedPreferences("option-cached", AppCompatActivity.MODE_PRIVATE)
    if (pref.contains("packages-json")) {
      val packagesJSON = JSONArray(pref.getString("packages-json", "[]"))
      for (i in 0 until packagesJSON.length()) {
        val pkg = packagesJSON.getJSONObject(i)
        packages[pkg.getString("dollar")] =
          PackageCls(
            pkg.getString("id").toInt(),
            pkg.getString("btc_usd"),
            pkg.getString("ltc_usd"),
            pkg.getString("eth_usd"),
            pkg.getString("doge_usd"),
            pkg.getString("camel_usd")
          )
      }
      val keys = Array(packages.size) { "" }
      var i = 0
      packages.forEach { (k, _) -> keys[i++] = k }
      typePackages.adapter =
        ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, keys)
      updatePrice()
    } else {
      dismiss()
      Toast.makeText(
        context, "cannot find packages, please restart application", Toast.LENGTH_SHORT
      )
        .show()
    }
  }

  private fun updatePrice() {
    val type =
      when (typeCurrency.selectedItem) {
        "BitCoin" -> "btc"
        "LiteCoin" -> "ltc"
        "Ethereum" -> "eth"
        "DogeCoin" -> "doge"
        "Camel" -> "camel"
        else -> "btc"
      }
    val pkg = packages[typePackages.selectedItem]
    val formula =
      fun(_: Int, coin: String): Any {
        return CoinFormat.decimalToCoin(BigDecimal(coin))
      }
    val tPrice =
      when (type) {
        "btc" -> formula(typePackages.selectedItem.toString().toInt(), pkg?.btc!!)
        "ltc" -> formula(typePackages.selectedItem.toString().toInt(), pkg?.ltc!!)
        "eth" -> formula(typePackages.selectedItem.toString().toInt(), pkg?.eth!!)
        "doge" -> formula(typePackages.selectedItem.toString().toInt(), pkg?.doge!!)
        else ->
          formula(
            typePackages.selectedItem.toString().toInt(),
            CoinFormat.coinToDecimal(BigDecimal(pkg?.camel!!)).toEngineeringString()
          )
      }.toString() + " " + type.toUpperCase(Locale.ROOT)
    price.setText(tPrice)
  }

  private fun upgrade() {
    val type =
      when (typeCurrency.selectedItem) {
        "BitCoin" -> "btc"
        "LiteCoin" -> "ltc"
        "Ethereum" -> "eth"
        "DogeCoin" -> "doge"
        "Camel" -> "camel"
        else -> "btc"
      }

    if (type in arrayOf("btc", "ltc", "eth") &&
      typePackages.selectedItem.toString().toInt() <= 1000
    ) {
      ownerActivity?.runOnUiThread {
        Toast.makeText(context, "Minimum upgrade with $type is $ 1000", Toast.LENGTH_LONG).show()
      }
    } else {
      val softKey = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
      softKey.hideSoftInputFromWindow(View(ownerActivity).windowToken, 0)
      val pass = secondaryPassword.text.toString()
      val balance = user.getString("balance_$type")
      val balanceFake = user.getString("fake_balance_$type")
      val body = FormBody.Builder()
      body.add("type", type)
      body.add("upgrade_list", packages[typePackages.selectedItem]?.id.toString())
      body.add("balance", balance)
      body.add("balance_fake", balanceFake)
      body.add("secondary_password", pass)
      Timer().schedule(100) {
        val response = PostController("upgrade.store", user.getString("token"), body).call()
        ownerActivity?.runOnUiThread {
          Toast.makeText(context, response.getString("data"), Toast.LENGTH_LONG).show()
          user.setBoolean("on_queue", true)
          if (response.getInt("code") != 500) dismiss()
        }
      }
    }
  }
}
