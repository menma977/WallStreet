package info.wallstreet.view.modal

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import info.wallstreet.R
import info.wallstreet.model.User

object WalletQR {
  private val negativeButtonClick = { _: DialogInterface, _: Int -> }

  fun show(context: Context, currency: String, user: User) {
    val builder = AlertDialog.Builder(context)
    val wallet = user.getString("wallet_$currency")
    val layout: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layoutWithdraw = layout.inflate(R.layout.modal_qr_layout, null)
    val textWallet = layoutWithdraw.findViewById(R.id.wallet_text) as TextView
    val imgWallet = layoutWithdraw.findViewById(R.id.qrcode) as ImageView
    val title = layoutWithdraw.findViewById(R.id.title) as TextView
    val icon = layoutWithdraw.findViewById(R.id.ic_currency) as ImageView
    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.encodeBitmap(wallet, BarcodeFormat.QR_CODE, 500, 500)
    when (currency) {
      "btc" -> {
        title.text = context.resources.getText(R.string.btc_wallet)
        icon.setImageResource(R.drawable.ic_btc)
      }
      "ltc" -> {
        title.text = context.resources.getText(R.string.ltc_wallet)
        icon.setImageResource(R.drawable.ic_ltc)
      }
      "eth" -> {
        title.text = context.resources.getText(R.string.eth_wallet)
        icon.setImageResource(R.drawable.ic_eth)
      }
      "doge" -> {
        title.text = context.resources.getText(R.string.doge_wallet)
        icon.setImageResource(R.drawable.ic_doge)
      }
      "camel" -> {
        title.text = context.resources.getText(R.string.camel_wallet)
        icon.setImageResource(R.drawable.ic_camel)
      }
    }
    imgWallet.setImageBitmap(bitmap)
    textWallet.text = wallet
    val positiveButtonClick = { _: DialogInterface, _: Int ->
      val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("Wallet", wallet)
      clipboardManager.setPrimaryClip(clipData)
      Toast.makeText(context, "Wallet has been copied", Toast.LENGTH_SHORT).show()
    }
    builder.setView(layoutWithdraw)
    builder.setPositiveButton("Copy", DialogInterface.OnClickListener(positiveButtonClick))
    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener(negativeButtonClick))
    builder.show()
  }
}