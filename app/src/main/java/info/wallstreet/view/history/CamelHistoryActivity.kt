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
import info.wallstreet.config.Loading
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
  private lateinit var loading: Loading
  private var page = 0
  private var _page = page
  private lateinit var type: String
  private lateinit var prev_btn: TextView
  private lateinit var next_btn: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)
    user = User(applicationContext)
    loading = Loading(this)

    icon = findViewById(R.id.icon)
    title = findViewById(R.id.textViewTitle)
    prev_btn = findViewById(R.id.prev_btn)
    next_btn = findViewById(R.id.next_btn)
    type = savedInstanceState?.getString("type") ?: "upgrade"
    icon.visibility = View.GONE

    title.text = "Camel History"
    listAdapter = HistoryCamelBalanceAdapter(this)
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
      val result = PostController(
        "camel.history?page=$page", user.getString("token"), FormBody.Builder()
      ).call()
      if (result.getInt("code") == 200) {
        val newData = result.getJSONObject("data").getJSONObject("list").getJSONArray("data")
        if (newData.length() > 0) {
          runOnUiThread {
            listAdapter.clear()
            for (i in newData.length() - 1 downTo 0) {
              val history = newData[i] as JSONObject
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                listAdapter.addItem(
                  HistoryCamelBalance(
                    history.getString("wallet"), history.getString("value"), history.getString("type"), LocalDateTime.parse(
                      history.getString("created_at").replace("T", " ").substring(0..18), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    ).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                  )
                )
              } else {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT)
                listAdapter.addItem(
                  HistoryCamelBalance(
                    history.getString("wallet"), history.getString("value"), history.getString("type"), formatter.format(
                      parser.parse(
                        history.getString(
                          "created_at"
                        )
                      ) ?: "-"
                    )
                  )
                )
              }
            }
            loading.closeDialog()
          }
        } else {
          page = _page
          runOnUiThread {
            Toast.makeText(applicationContext, "No more page to show", Toast.LENGTH_SHORT).show()
            loading.closeDialog()
          }
        }
      } else {
        page = _page
        runOnUiThread {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }.start()
  }
}