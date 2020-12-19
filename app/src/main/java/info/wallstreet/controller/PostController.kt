package info.wallstreet.controller

import android.util.Log
import info.wallstreet.model.Url
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Callable

class PostController(private var targetUrl: String, private var token: String?, private var bodyValue: FormBody.Builder) : Callable<JSONObject> {
  constructor(targetUrl: String, bodyValue: FormBody.Builder) : this(targetUrl, null, bodyValue)

  companion object {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun responseHandler(response: Response, jsonObject: JSONObject): JSONObject {
      if (response.isSuccessful) {
        return when {
          jsonObject.toString().contains("message") -> {
            JSONObject().put("code", 200).put("data", jsonObject.getString("message")).put("logout", false)
          }
          else -> {
            JSONObject().put("code", 200).put("data", jsonObject).put("logout", false)
          }
        }
      } else {
        return when {
          jsonObject.toString().contains("Unauthenticated.") -> {
            JSONObject().put("code", 500).put("data", jsonObject.getString("message")).put("logout", true)
          }
          jsonObject.toString().contains("errors") -> {
            JSONObject().put("code", 500).put("data", jsonObject.getJSONObject("errors").getJSONArray(jsonObject.getJSONObject("errors").names()[0].toString())[0]).put("logout", false)
          }
          jsonObject.toString().contains("message") -> {
            JSONObject().put("code", 500).put("data", jsonObject.getString("message")).put("logout", false)
          }
          else -> {
            JSONObject().put("code", 500).put("data", jsonObject).put("logout", false)
          }
        }
      }
    }

    fun render(response: Response): String {
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      return input.readLine()
    }
  }

  override fun call(): JSONObject {
    return try {
      val client = OkHttpClient.Builder().build()
      val request = Request.Builder()
      request.url(Url.web(targetUrl))
      request.post(bodyValue.build())
      if (!token.isNullOrEmpty()) {
        request.addHeader("Authorization", "Bearer ${token!!}")
      }

      request.addHeader("charset", "utf-8")
      request.addHeader("Content-Type", "application/json")
      request.addHeader("Accept", "application/json")
      request.addHeader("X-Requested-With", "XMLHttpRequest")
      val response = client.newCall(request.build()).execute()
      val convertJSON = JSONObject(render(response))
      Log.i("json", convertJSON.toString())

      return responseHandler(response, convertJSON)
    } catch (e: Exception) {
      Log.e("json", e.stackTraceToString())
      JSONObject().put("code", 500).put("data", e.message).put("logout", false)
    }
  }
}