package info.wallstreet.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.UpgradeHistory
import java.util.*
import kotlin.collections.ArrayList

class UpgradeListAdapter(private val context: Context) :
  RecyclerView.Adapter<UpgradeListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<UpgradeHistory>()

  init {
    myDataset.add(UpgradeHistory("", "", Date()))
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): MyViewHolder {
    val layout =
      LayoutInflater.from(parent.context).inflate(R.layout.list_upgrade_history, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    if (position == 0) return
    (holder.currency).setImageResource(
      when (myDataset[position].type) {
        "btc" -> R.drawable.ic_btc
        "ltc" -> R.drawable.ic_ltc
        "eth" -> R.drawable.ic_eth
        "doge" -> R.drawable.ic_doge
        else -> R.drawable.ic_btc
      }
    )
    holder.currency.visibility = View.VISIBLE
    holder.currencyHeader.text =
      myDataset[position].type.toUpperCase(Locale.getDefault())
    holder.level.text = myDataset[position].level
    holder.date.text = myDataset[position].date.toString()
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: UpgradeHistory) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val currency: ImageView = layout.findViewById(R.id.currency)
    val currencyHeader: TextView = layout.findViewById(R.id.currency_header)
    val level: TextView = layout.findViewById(R.id.level)
    val date: TextView = layout.findViewById(R.id.date)
  }
}