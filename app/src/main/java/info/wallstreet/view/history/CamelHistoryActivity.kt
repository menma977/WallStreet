package info.wallstreet.view.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.controller.GetController
import info.wallstreet.model.HistoryCamelBalance
import info.wallstreet.model.UpgradeHistory
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryCamelBalanceAdapter
import info.wallstreet.view.adapter.UpgradeListAdapter
import org.json.JSONObject

class CamelHistoryActivity : AppCompatActivity() {
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var icon: ImageView
  private lateinit var listAdapter: HistoryCamelBalanceAdapter
  private lateinit var user: User
  private var page = 1
  private var _page = page
  private lateinit var type: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)
    user = User(applicationContext)

    title = findViewById(R.id.textViewTitle)
    type = savedInstanceState?.getString("type") ?: "upgrade"
    icon.visibility = View.GONE

    title.text = "Camel History"
    listAdapter = HistoryCamelBalanceAdapter(this)
    listView = findViewById<RecyclerView>(R.id.lists_container).apply {
      layoutManager = LinearLayoutManager(applicationContext)
      adapter = listAdapter
    }
    rePopulate()
  }

  private fun rePopulate() {
    _page = page++
    Thread {
      val result = GetController("camel.history?page=$page", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        val newData = result.getJSONObject("data").getJSONObject("list").getJSONArray("data")
        if (newData.length() > 0) {
          runOnUiThread {
            listAdapter.clear()
            for (i in 0 until newData.length()) {
              val history = newData[i] as JSONObject
              listAdapter.addItem(
                HistoryCamelBalance(history.getString("wallet"))
              )
            }
          }
        } else {
          page = _page
          runOnUiThread {
            Toast.makeText(applicationContext, "No more page to show", Toast.LENGTH_SHORT).show()
          }
        }
      } else {
        page = _page
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
        }
      }
    }.start()
  }
}