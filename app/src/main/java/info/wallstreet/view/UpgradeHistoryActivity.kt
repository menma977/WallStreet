package info.wallstreet.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.controller.GetController
import info.wallstreet.model.UpgradeHistory
import info.wallstreet.view.adapter.UpgradeListAdapter
import org.json.JSONObject
import java.util.*

class UpgradeHistoryActivity : AppCompatActivity() {
  private lateinit var listView: RecyclerView
  private lateinit var title: TextView
  private lateinit var listAdapter: UpgradeListAdapter
  private var page = 1

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
    rePopulate()
  }

  private fun rePopulate(){
    Thread {
      val result = GetController("upgrade.show?page=$page").call()
      if(result.getInt("code")==200){
        val newData = result.getJSONObject("data").getJSONArray("upgrades")
        if(newData.length() > 0){
          page++
          listAdapter.clear()
          for(i in 0 until newData.length()){
            val history = newData[i] as JSONObject
            listAdapter.addItem(UpgradeHistory(
              history.getString("debit"),
              history.getString("description"),
              history.getString("created_at")
            ))
          }
        }
      }
    }.start()
  }
}