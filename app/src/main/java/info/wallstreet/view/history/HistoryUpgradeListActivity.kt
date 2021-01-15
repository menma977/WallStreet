package info.wallstreet.view.history

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.UpgradeList
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryUpgradeListAdapter
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class HistoryUpgradeListActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var buttonPreview: Button
  private lateinit var buttonNext: Button
  private lateinit var listView: RecyclerView
  private lateinit var listAdapter: HistoryUpgradeListAdapter
  private lateinit var result: JSONObject
  private var prevPageUrl = ""
  private var nextPageUrl = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history_upgrade_list)

    user = User(this)
    loading = Loading(this)

    buttonPreview = findViewById(R.id.buttonPreview)
    buttonNext = findViewById(R.id.buttonNext)

    loading.openDialog()

    listAdapter = HistoryUpgradeListAdapter()
    listView = findViewById<RecyclerView>(R.id.lists_container).apply {
      layoutManager = LinearLayoutManager(applicationContext)
      adapter = listAdapter
    }

    listAdapter.clear()

    getData()

    buttonPreview.setOnClickListener {
      loading.openDialog()
      readUlr(prevPageUrl)
    }

    buttonNext.setOnClickListener {
      loading.openDialog()
      readUlr(nextPageUrl)
    }
  }

  private fun getData() {
    listAdapter.clear()
    Timer().schedule(100) {
      result = GetController("upgrade.list", user.getString("token")).call()
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

  private fun readUlr(urlTarget: String) {
    listAdapter.clear()
    Timer().schedule(100) {
      result = GetController("upgrade.list?page=$urlTarget", user.getString("token")).call()
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
    println(data)
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

    for (i in 0 until list.length() - 1) {
      val read = list.getJSONObject(i)
      runOnUiThread {
        listAdapter.addItem(UpgradeList("$ ${read.getString("balance")}", read.getString("type"), read.getString("date")))
      }
    }

    runOnUiThread {
      loading.closeDialog()
    }
  }
}