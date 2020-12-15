package info.wallstreet.config

import android.R.style.Theme_Translucent_NoTitleBar
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import info.wallstreet.R

@SuppressLint("InflateParams")
class Loading(activity: Activity) {
  private val dialog = Dialog(activity, Theme_Translucent_NoTitleBar)

  init {
    val view = activity.layoutInflater.inflate(R.layout.activity_main, null)
    dialog.setContentView(view)
    dialog.setCancelable(false)
  }

  fun openDialog() {
    dialog.show()
  }

  fun closeDialog() {
    dialog.dismiss()
  }
}