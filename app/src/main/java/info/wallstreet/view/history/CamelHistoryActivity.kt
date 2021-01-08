package info.wallstreet.view.history

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.controller.PostController
import info.wallstreet.model.HistoryCamelBalance
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryCamelBalanceAdapter
import okhttp3.FormBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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

    icon = findViewById(R.id.icon)
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
      val result = PostController(
        "camel.history?page=$page",
        user.getString("token"),
        FormBody.Builder()
      ).call()
      if (result.getInt("code") == 200) {
        val newData = result.getJSONObject("data").getJSONObject("list").getJSONArray("data")
        if (newData.length() > 0) {
          runOnUiThread {
            listAdapter.clear()
            for (i in newData.length()-1 downTo 0) {
              val history = newData[i] as JSONObject
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                listAdapter.addItem(
                  HistoryCamelBalance(
                    history.getString("wallet"), history.getString("value"), LocalDateTime.parse(
                      history.getString(
                        "created_at"
                      ),
                      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    ).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                  )
                )
              }else{
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT)
                listAdapter.addItem(
                  HistoryCamelBalance(
                    history.getString("wallet"), history.getString("value"), formatter.format(parser.parse(
                      history.getString(
                        "created_at"
                      )) ?: "-"
                    )
                  )
                )
              }
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