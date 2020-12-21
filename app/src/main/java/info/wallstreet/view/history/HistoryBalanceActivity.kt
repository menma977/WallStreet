package info.wallstreet.view.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.model.User
import info.wallstreet.view.adapter.HistoryFakeBalanceListAdapter
import org.json.JSONObject

class HistoryBalanceActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var buttonIn: Button
  private lateinit var buttonOut: Button
  private lateinit var buttonInternal: Button
  private lateinit var buttonExternal: Button
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
    setContentView(R.layout.activity_history_balance)

    user = User(this)
    loading = Loading(this)

    buttonIn = findViewById(R.id.buttonIn)
    buttonOut = findViewById(R.id.buttonOut)
    buttonInternal = findViewById(R.id.buttonInternal)
    buttonExternal = findViewById(R.id.buttonExternal)
    buttonPreview = findViewById(R.id.buttonPreview)
    buttonNext = findViewById(R.id.buttonNext)
    title = findViewById(R.id.textViewTitle)
  }
}