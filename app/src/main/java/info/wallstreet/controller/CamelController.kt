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

object CamelController {
  private fun failResponseHandler(content: JSONObject): JSONObject{
    return JSONObject("{code:500, data:\"ERROR MESSAGE: Change Me\"}")
  }

  private fun get(url:String): JSONObject{
    val client = OkHttpClient.Builder()
    client.connectTimeout(30, TimeUnit.SECONDS)
    client.writeTimeout(30, TimeUnit.SECONDS)
    client.readTimeout(30, TimeUnit.SECONDS)
    val request = Request.Builder()
    request.addHeader("Access-Control-Allow-Origin", "*")
    request.addHeader("X-Requested-With", "XMLHttpRequest")
    request.addHeader("Connection", "close")
    request.header("Connection", "close")
    request.url(url)
    request.get()
    val response: Response = client.build().newCall(request.build()).execute()
    val inputReader = BufferedReader(InputStreamReader(response.body!!.byteStream()))
    var rawContent = ""
    var line = inputReader.readLine()
    while(line != null){
      rawContent += line
      line = inputReader.readLine()
    }
    val content = JSONObject(rawContent)
    return when {
      response.isSuccessful ->
        JSONObject("{code: 200}").put("data", content)
      else ->
        failResponseHandler(content)
    }
  }

  public fun getBalance(address: String): JSONObject {
    return get(Url.camel("/getbalance/$address"))
  }

  public fun getTokenBalance(address: String): JSONObject {
    return get(Url.camel("/gettokenbalance/$address"))
  }

}