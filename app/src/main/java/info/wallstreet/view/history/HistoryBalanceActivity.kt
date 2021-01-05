package info.wallstreet.view.history

import android.content.Intent
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
import info.wallstreet.controller.DogeController
import info.wallstreet.model.HistoryExternalBalance
import info.wallstreet.model.HistoryInternalBalance
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryExternalBalanceListAdapter
import info.wallstreet.view.adapter.HistoryInternalBalanceListAdapter
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class HistoryBalanceActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var buttonIn: Button
  private lateinit var buttonOut: Button
  private lateinit var buttonInternal: Button
  private lateinit var buttonExternal: Button
  private lateinit var buttonNext: Button
  private lateinit var buttonCamelHistory: Button
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var result: JSONObject
  private lateinit var listAdapterInternal: HistoryInternalBalanceListAdapter
  private lateinit var listAdapterExternal: HistoryExternalBalanceListAdapter
  private var targetUrl = ""
  private var newToken = ""
  private var typeList = "in"
  private var typeExternal = ""
  private var isIn = false
  private var isInternal = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history_balance)

    user = User(this)
    loading = Loading(this)

    buttonIn = findViewById(R.id.buttonIn)
    buttonOut = findViewById(R.id.buttonOut)
    buttonInternal = findViewById(R.id.buttonInternal)
    buttonExternal = findViewById(R.id.buttonExternal)
    buttonNext = findViewById(R.id.buttonNext)
    buttonCamelHistory = findViewById(R.id.buttonCamelHistory)
    title = findViewById(R.id.textViewTitle)

    title.text = "-"
    listAdapterInternal = HistoryInternalBalanceListAdapter(this)
    listAdapterExternal = HistoryExternalBalanceListAdapter(this)

    buttonIn.setOnClickListener {
      targetUrl = "GetDeposits"
      buttonInternal.isEnabled = true
      buttonExternal.isEnabled = true
      typeExternal = "Deposits"
      newToken = ""
      isIn = true
      title.text = "IN"
      typeList = "in"
    }

    buttonOut.setOnClickListener {
      targetUrl = "GetWithdrawals"
      buttonInternal.isEnabled = true
      buttonExternal.isEnabled = true
      typeExternal = "Withdrawals"
      newToken = ""
      isIn = false
      title.text = "OUT"
      typeList = "out"
    }

    buttonInternal.setOnClickListener {
      loading.openDialog()
      isInternal = true
      listView = findViewById<RecyclerView>(R.id.lists_container).apply {
        layoutManager = LinearLayoutManager(applicationContext)
        adapter = listAdapterInternal
      }
      getDataInternal()
    }

    buttonExternal.setOnClickListener {
      loading.openDialog()
      isInternal = false
      listView = findViewById<RecyclerView>(R.id.lists_container).apply {
        layoutManager = LinearLayoutManager(applicationContext)
        adapter = listAdapterExternal
      }
      getDataExternal(typeExternal)
    }

    buttonNext.setOnClickListener {
      loading.openDialog()
      if (isInternal) {
        getDataInternal()
      } else {
        getDataExternal(typeExternal)
      }
    }

    buttonCamelHistory.setOnClickListener {
      val move = Intent(this, CamelHistoryActivity::class.java)
      startActivity(move)
    }

    listAdapterInternal.clear()
    listAdapterExternal.clear()
  }

  private fun getDataInternal() {
    listAdapterInternal.clear()
    if (isIn) {
      title.text = "IN - Internal"
    } else {
      title.text = "OUT - Internal"
    }
    Timer().schedule(100) {
      val body = FormBody.Builder()
      body.addEncoded("a", targetUrl)
      body.addEncoded("s", user.getString("cookie"))
      body.addEncoded("Token", newToken)
      result = DogeController(body).call()

      if (result.getInt("code") == 200) {
        val list = result.getJSONObject("data")
        runOnUiThread {
          if (list.getString("Token").toString().isNotEmpty()) {
            newToken = list.getString("Token")
            buttonNext.isEnabled = true
          } else {
            buttonNext.isEnabled = false
          }
        }
        val listArray = list.getJSONArray("Transfers")

        for (i in 0 until listArray.length()) {
          val read = listArray.getJSONObject(i)
          val balanceFormat = "-" + CoinFormat.decimalToCoin(read.getString("Value").toBigDecimal()).toPlainString()
          val date = try {
            read.getString("Date")
          } catch (e: Exception) {
            read.getString("Completed")
          }
          runOnUiThread {
            listAdapterInternal.addItem(
              HistoryInternalBalance(
                typeList,
                read.getString("Address").replace("XFER", "WALL"),
                balanceFormat,
                read.getString("Currency"),
                date,
              )
            )
          }
        }

        runOnUiThread {
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun getDataExternal(nameList: String) {
    listAdapterExternal.clear()
    if (isIn) {
      title.text = "IN - External"
    } else {
      title.text = "OUT - External"
    }
    Timer().schedule(100) {
      val body = FormBody.Builder()
      body.addEncoded("a", targetUrl)
      body.addEncoded("s", user.getString("cookie"))
      body.addEncoded("Token", newToken)
      result = DogeController(body).call()

      if (result.getInt("code") == 200) {
        val list = result.getJSONObject("data")
        runOnUiThread {
          if (list.getString("Token").toString().isNotEmpty()) {
            newToken = list.getString("Token")
            buttonNext.isEnabled = true
          } else {
            buttonNext.isEnabled = false
          }
        }
        val listArray = list.getJSONArray(nameList)

        for (i in 0 until listArray.length()) {
          val read = listArray.getJSONObject(i)
          val balanceFormat = "+" + CoinFormat.decimalToCoin(read.getString("Value").toBigDecimal()).toPlainString()
          val date = try {
            read.getString("Date")
          } catch (e: Exception) {
            read.getString("Completed")
          }
          runOnUiThread {
            listAdapterExternal.addItem(
              HistoryExternalBalance(
                typeList,
                read.getString("Address").replace("XFER", "WALL"),
                read.getString("TransactionHash"),
                balanceFormat,
                read.getString("Currency"),
                date,
              )
            )
          }
        }

        runOnUiThread {
          loading.closeDialog()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }
}