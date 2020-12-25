package info.wallstreet.view.history

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.config.CoinFormat
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.HistoryFakeBalance
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryFakeBalanceListAdapter
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class FakeBalanceActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var buttonBTC: Button
  private lateinit var buttonETH: Button
  private lateinit var buttonDoge: Button
  private lateinit var buttonLTC: Button
  private lateinit var buttonCamel: Button
  private lateinit var buttonPreview: Button
  private lateinit var buttonNext: Button
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var listAdapter: HistoryFakeBalanceListAdapter
  private lateinit var result: JSONObject
  private var currencyUrl = ""
  private var prevPageUrl = ""
  private var nextPageUrl = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fake_balance)

    user = User(this)
    loading = Loading(this)

    buttonBTC = findViewById(R.id.buttonBTC)
    buttonETH = findViewById(R.id.buttonETH)
    buttonDoge = findViewById(R.id.buttonDoge)
    buttonLTC = findViewById(R.id.buttonLTC)
    buttonCamel = findViewById(R.id.buttonCamel)
    buttonPreview = findViewById(R.id.buttonPreview)
    buttonNext = findViewById(R.id.buttonNext)
    title = findViewById(R.id.textViewTitle)

    title.text = "-"
    listAdapter = HistoryFakeBalanceListAdapter(this)
    listView = findViewById<RecyclerView>(R.id.lists_container).apply {
      layoutManager = LinearLayoutManager(applicationContext)
      adapter = listAdapter
    }

    buttonBTC.setOnClickListener {
      loading.openDialog()
      getData("btc")
    }

    buttonETH.setOnClickListener {
      loading.openDialog()
      getData("eth")
    }

    buttonDoge.setOnClickListener {
      loading.openDialog()
      getData("doge")
    }

    buttonLTC.setOnClickListener {
      loading.openDialog()
      getData("ltc")
    }

    buttonCamel.setOnClickListener {
      loading.openDialog()
      getData("camel")
    }

    buttonPreview.setOnClickListener {
      loading.openDialog()
      readUlr(currencyUrl, prevPageUrl)
    }

    buttonNext.setOnClickListener {
      loading.openDialog()
      readUlr(currencyUrl, nextPageUrl)
    }

    listAdapter.clear()
  }

  private fun getData(type: String) {
    listAdapter.clear()
    currencyUrl = type
    title.text = type.toUpperCase()
    Timer().schedule(100) {
      result = GetController("$type.show", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        generateResult(result)
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun readUlr(currency: String, urlTarget: String) {
    listAdapter.clear()
    Timer().schedule(100) {
      result = GetController("$currency.show?page=$urlTarget", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        generateResult(result)
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun generateResult(result: JSONObject) {
    val data = result.getJSONObject("data").getJSONObject("list")
    val list = data.getJSONArray("data")
    runOnUiThread {
      if (data.getString("prev_page_url").toString() != "null") {
        prevPageUrl = (data.getInt("current_page") - 1).toString()
        buttonPreview.isEnabled = true
      } else {
        buttonPreview.isEnabled = false
      }

      if (data.getString("next_page_url").toString() != "null") {
        nextPageUrl = (data.getInt("current_page") + 1).toString()
        buttonNext.isEnabled = true
      } else {
        buttonNext.isEnabled = false
      }
    }

    for (i in 0 until list.length()) {
      val read = list.getJSONObject(i)
      val balanceFormat = CoinFormat.decimalToCoin(read.getString("balance").toBigDecimal()).toPlainString()
      runOnUiThread {
        listAdapter.addItem(HistoryFakeBalance(read.getString("color"), read.getString("description"), balanceFormat, read.getString("date")))
      }
    }

    runOnUiThread {
      loading.closeDialog()
    }
  }
}