package info.wallstreet.view.history

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.GetController
import info.wallstreet.model.UpgradeHistory
import info.wallstreet.model.User
import info.wallstreet.view.adapter.UpgradeListAdapter
import org.json.JSONObject

class UpgradeHistoryActivity : AppCompatActivity() {
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var listAdapter: UpgradeListAdapter
  private lateinit var user: User
  private lateinit var loading: Loading
  private var page = 1
  private var _page = page
  private lateinit var type: String
  private lateinit var prev_btn: TextView
  private lateinit var next_btn: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)
    user = User(applicationContext)
    loading = Loading(this)

    title = findViewById(R.id.textViewTitle)
    prev_btn = findViewById(R.id.prev_btn)
    next_btn = findViewById(R.id.next_btn)
    type = savedInstanceState?.getString("type") ?: "upgrade"

    title.text = "Upgrades History"
    listAdapter = UpgradeListAdapter(this)
    listView = findViewById<RecyclerView>(R.id.lists_container).apply {
      layoutManager = LinearLayoutManager(applicationContext)
      adapter = listAdapter
    }
    prev_btn.setOnClickListener { rePopulate(-1) }
    next_btn.setOnClickListener { rePopulate() }
    rePopulate()
  }

  private fun rePopulate(nextPage: Int = 1) {
    loading.openDialog()
    _page = page;
    page += nextPage;
    if (page <= 0) {
      loading.closeDialog()
      return Toast.makeText(applicationContext, "No more page to show", Toast.LENGTH_SHORT).show()
    }
    Thread {
      val result = GetController("upgrade.show?page=$page", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        val newData = result.getJSONObject("data").getJSONObject("list").getJSONArray("data")
        if (newData.length() > 0) {
          runOnUiThread {
            listAdapter.clear()
            for (i in 0 until newData.length()) {
              val history = newData[i] as JSONObject
              listAdapter.addItem(
                UpgradeHistory(history.getString("balance"), history.getString("description"), history.getString("date"), history.getString("color"))
              )
            }
            loading.closeDialog()
          }
        } else {
          page = _page
          runOnUiThread {
            loading.closeDialog()
            Toast.makeText(applicationContext, "No more page to show", Toast.LENGTH_SHORT).show()
          }
        }
      } else {
        page = _page
        runOnUiThread {
          loading.closeDialog()
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
        }
      }
    }.start()
  }
}