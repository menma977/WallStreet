package info.wallstreet.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import info.wallstreet.R
import info.wallstreet.config.Loading
import info.wallstreet.controller.WebViewController
import info.wallstreet.model.Url
import info.wallstreet.model.User
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class BinaryActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var webContent: WebView
  private lateinit var result: JSONObject

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_binary)

    user = User(this)
    loading = Loading(this)

    webContent = findViewById(R.id.webViewContent)

    loading.openDialog()
    Timer().schedule(100) {
      result = WebViewController("binary", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        runOnUiThread {
          webContent.removeAllViews()
          webContent.webViewClient = WebViewClient()
          webContent.webChromeClient = WebChromeClient()
          webContent.settings.javaScriptEnabled = true
          webContent.settings.domStorageEnabled = true
          webContent.settings.javaScriptCanOpenWindowsAutomatically = true
          webContent.loadData(result.getString("data"), "text/html", "UTF-8")
          webContent.loadDataWithBaseURL(Url.web("binary"), result.getString("data"), "text/html", "UTF-8", null)
          loading.closeDialog()
        }
      }
    }
  }
}