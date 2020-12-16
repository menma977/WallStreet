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

class GetController(private var targetUrl: String, private var token: String?, private var bodyValue: FormBody.Builder?) : Callable<JSONObject> {
  constructor(targetUrl: String, bodyValue: FormBody.Builder) : this(targetUrl, null, bodyValue)
  constructor(targetUrl: String, token: String) : this(targetUrl, token, null)
  constructor(targetUrl: String) : this(targetUrl, null, null)

  companion object {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun responseHandler(response: Response, jsonObject: JSONObject): JSONObject {
      if (response.isSuccessful && response.code == 200) {
        return when {
          jsonObject.toString().contains("message") -> {
            JSONObject().put("code", 200).put("data", jsonObject.getString("message")).put("logout", false)
          }
          jsonObject.toString().contains("Unauthenticated.") -> {
            JSONObject().put("code", 200).put("data", jsonObject).put("logout", true)
          }
          else -> {
            JSONObject().put("code", 200).put("data", jsonObject).put("logout", false)
          }
        }
      } else {
        return when {
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

    fun render(response: Response): JSONObject {
      val input = BufferedReader(InputStreamReader(response.body!!.byteStream()))
      val inputData: String = input.readLine()
      return JSONObject(inputData)
    }
  }

  override fun call(): JSONObject {
    return try {
      val client = OkHttpClient.Builder().build()
      val request = Request.Builder()
      request.url(Url.web(targetUrl))
      request.method("GET", if (bodyValue != null) bodyValue!!.build() else null)
      if (!token.isNullOrEmpty()) {
        request.addHeader("Authorization", token!!)
      }

      request.addHeader("X-Request-With", "XMLHttpRequest")
      val response = client.newCall(request.build()).execute()
      val convertJSON = render(response)
      Log.i("json", convertJSON.toString())

      return responseHandler(response, convertJSON)
    } catch (e: Exception) {
      Log.e("json", e.stackTraceToString())
      JSONObject().put("code", 500).put("data", e.message)
    }
  }
}