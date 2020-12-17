package info.wallstreet.controller

import info.wallstreet.model.Url
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class WebViewController(private var targetUrl: String, private var token: String, private var bodyValue: FormBody.Builder) : Callable<JSONObject> {
  override fun call(): JSONObject {
    return try {
      val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
      val request = Request.Builder()
      request.url(Url.web(targetUrl))
      request.addHeader("X-Requested-With", "XMLHttpRequest")
      if (token.isNotEmpty()) {
        request.addHeader("Authorization", "Bearer $token")
      }
      request.post(bodyValue.build())
      val response: Response = client.newCall(request.build()).execute()
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      return JSONObject().put("code", 200).put("data", input.readText())
    } catch (e: Exception) {
      JSONObject().put("code", 500).put("data", e.message).put("logout", false)
    }
  }
}