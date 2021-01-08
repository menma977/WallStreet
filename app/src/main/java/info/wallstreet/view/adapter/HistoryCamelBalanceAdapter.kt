package info.wallstreet.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.HistoryCamelBalance

class HistoryCamelBalanceAdapter(private val context: Context) : RecyclerView.Adapter<HistoryCamelBalanceAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<HistoryCamelBalance>()

  init {
    myDataset.add(HistoryCamelBalance("Wallet", "Value", "Date"))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_camel_balance, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.wallet.text = myDataset[position].wallet
    holder.value.text = myDataset[position].value
    holder.wallet.setOnClickListener {
      val move = Intent(Intent.ACTION_VIEW, Uri.parse("https://tronscan.io/#/address/" + myDataset[position].wallet))
      context.startActivity(move)
    }
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: HistoryCamelBalance) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(HistoryCamelBalance("Wallet", "Value","Date"))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val wallet: TextView = layout.findViewById(R.id.wallet)
    val value: TextView = layout.findViewById(R.id.value)
  }
}