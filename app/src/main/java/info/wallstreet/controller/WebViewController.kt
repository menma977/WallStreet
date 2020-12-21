package info.wallstreet.controller

import info.wallstreet.model.Url
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Callable

class WebViewController(private var targetUrl: String, private var token: String) : Callable<JSONObject> {
  override fun call(): JSONObject {
    return try {
      val client = OkHttpClient.Builder().build()
      val request = Request.Builder()
      request.url(Url.web(targetUrl))
      request.addHeader("X-Requested-With", "XMLHttpRequest")
      if (token.isNotEmpty()) {
        request.addHeader("Authorization", "Bearer $token")
      }
      request.method("GET", null)
      val response: Response = client.newCall(request.build()).execute()
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      return JSONObject().put("code", 200).put("data", input.readText())
    } catch (e: Exception) {
      JSONObject().put("code", 500).put("data", e.message).put("logout", false)
    }
  }
}