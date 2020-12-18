package info.wallstreet.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.UpgradeHistory
import info.wallstreet.view.adapter.UpgradeListAdapter
import java.util.*

class UpgradeHistoryActivity : AppCompatActivity() {
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var listAdapter: UpgradeListAdapter

  private lateinit var type: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)
    title = findViewById(R.id.textViewTitle)
    type = savedInstanceState?.getString("type") ?: "upgrade"

    title.text = "Upgrades History"
    listAdapter = UpgradeListAdapter(applicationContext)
    listView = findViewById<RecyclerView>(R.id.lists_container).apply {
      layoutManager = LinearLayoutManager(applicationContext)
      adapter = listAdapter
    }

    //populate Adapter
    listAdapter.addItem(UpgradeHistory("btc", "1", Date()))
    listAdapter.addItem(UpgradeHistory("eth", "2", Date()))
    listAdapter.addItem(UpgradeHistory("btc", "3", Date()))
    listAdapter.addItem(UpgradeHistory("doge", "4", Date()))
  }
}